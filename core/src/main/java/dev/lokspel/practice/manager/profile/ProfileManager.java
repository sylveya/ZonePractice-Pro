package dev.lokspel.practice.manager.profile;

import dev.lokspel.api.Event.NewPlayerJoin;
import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.database.Database;
import dev.lokspel.practice.manager.backend.database.model.GlobalStatsRow;
import dev.lokspel.practice.manager.backend.database.model.LadderStatsRow;
import dev.lokspel.practice.manager.division.DivisionManager;
import dev.lokspel.practice.manager.ladder.LadderManager;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.profile.statistics.LadderStats;
import dev.lokspel.practice.manager.profile.statistics.ProfileStat;
import dev.lokspel.practice.manager.gui.guis.profile.ProfileSettingsGui;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.StartUpCallback;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileManager {

    private static ProfileManager instance;

    public static ProfileManager getInstance() {
        if (instance == null)
            instance = new ProfileManager();
        return instance;
    }

    private ProfileManager() {
    }

    @Getter
    private final Map<Player, UUID> uuids = new ConcurrentHashMap<>();
    @Getter
    private final Map<UUID, Profile> profiles = new ConcurrentHashMap<>();

    private final File folder = new File(AstralPractice.getInstance().getDataFolder() + "/profiles");


    public Profile getProfile(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        Profile cached = profiles.get(uuid);
        if (cached != null) {
            return cached;
        }

        return loadProfileIfExists(uuid, Bukkit.getOfflinePlayer(uuid));
    }

    public Profile getProfile(Player player) {
        if (player == null) {
            return null;
        }
        UUID uuid = player.getUniqueId();
        uuids.put(player, uuid);
        Profile profile = profiles.get(uuid);
        if (profile == null) {
            profile = loadProfileIfExists(uuid, player);
        }
        loadProfileInfo(profile);
        return profile;
    }

    public Profile getProfile(OfflinePlayer player) {
        if (player == null) return null;

        UUID uuid = player.getUniqueId();
        Profile profile = profiles.get(uuid);
        if (profile != null) {
            return profile;
        }

        return loadProfileIfExists(uuid, player);
    }

    public Profile getProfile(Entity entity) {
        if (entity == null) return null;
        if (entity instanceof Player)
            return getProfile((Player) entity);
        return null;
    }

    public Profile newProfile(Player player, UUID uuid) {
        Profile profile = new Profile(uuid);

        profile.getFile().setDefaultData();
        profile.load();

        profiles.put(uuid, profile);
        loadProfileInfo(profile);

        Bukkit.getScheduler().runTaskLater(AstralPractice.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new NewPlayerJoin(player)), 20L * 2);

        return profile;
    }

    public void loadProfiles(final StartUpCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(AstralPractice.getInstance(), () ->
        {
            // 1. Load YAML profiles: settings, kits, cosmetics, timestamps
            loadProfilesFromDisk();

            // 2. Overlay the database statistics: elo, wins, losses per ladder
            loadProfilesFromDatabase().whenComplete((ignored, throwable) -> {
                if (throwable != null) {
                    Common.sendConsoleMMMessage("<red>Error: " + throwable.getMessage());
                }
                Bukkit.getScheduler().runTask(AstralPractice.getInstance(), callback::onLoadingDone);
            });
        });
    }

    private CompletableFuture<Void> loadProfilesFromDatabase() {
        Database database = AstralPractice.getDatabase();
        if (database == null) return CompletableFuture.completedFuture(null);

        List<CompletableFuture<Void>> futures = new ArrayList<>(profiles.size());
        for (Profile profile : profiles.values()) {
            CompletableFuture<Void> future = database.getGlobalStatsRepository().getStats(profile.getUuid())
                    .thenAccept(row -> applyGlobalStats(row, profile))
                    .thenCompose(ignored -> database.getLadderStatsRepository().getStats(profile.getUuid()))
                    .thenAccept(rows -> applyLadderStats(rows, profile));

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static void applyGlobalStats(GlobalStatsRow row, Profile profile) {
        if (row == null) return;

        profile.setFirstJoin(row.getFirstJoin());
        profile.setLastJoin(row.getLastJoin());

        // The win/loss totals and the global ELO are derived from the ladder rows by
        // ProfileStat, so only the fields it does not recompute are restored here.
        ProfileStat stats = profile.getStats();
        stats.setExperience(row.getExperience());
        stats.setWinStreak(row.getWinStreak());
        stats.setBestWinStreak(row.getBestWinStreak());
        stats.setLoseStreak(row.getLoseStreak());
        stats.setBestLoseStreak(row.getBestLoseStreak());
    }

    private static void applyLadderStats(List<LadderStatsRow> rows, Profile profile) {
        for (LadderStatsRow row : rows) {
            NormalLadder ladder = LadderManager.getInstance().getLadder(row.getLadder());
            if (ladder == null) continue;

            LadderStats stats = profile.getStats().getLadderStat(ladder);
            stats.setUnRankedWins(row.getUnrankedWins());
            stats.setUnRankedLosses(row.getUnrankedLosses());
            stats.setUnRankedWinStreak(row.getUnrankedWinStreak());
            stats.setUnRankedBestWinStreak(row.getUnrankedBestWinStreak());
            stats.setUnRankedLoseStreak(row.getUnrankedLoseStreak());
            stats.setUnRankedBestLoseStreak(row.getUnrankedBestLoseStreak());
            stats.setRankedWins(row.getRankedWins());
            stats.setRankedLosses(row.getRankedLosses());
            stats.setRankedWinStreak(row.getRankedWinStreak());
            stats.setRankedBestWinStreak(row.getRankedBestWinStreak());
            stats.setRankedLoseStreak(row.getRankedLoseStreak());
            stats.setRankedBestLoseStreak(row.getRankedBestLoseStreak());
            stats.setElo(row.getElo());
            stats.setKills(row.getKills());
            stats.setDeaths(row.getDeaths());
        }
    }

    /** Iterates all .yml files in /profiles/ and loads each into the cache. */
    private void loadProfilesFromDisk() {
        if (!folder.exists() && !folder.mkdirs()) {
            Common.sendConsoleMMMessage("<red>Error: Could not create profiles folder.");
        }

        if (!folder.isDirectory()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File profileFile : files) {
            if (!profileFile.isFile() || !profileFile.getName().endsWith(".yml")) continue;

            UUID uuid = parseUuidFromFilename(profileFile.getName());
            if (uuid == null) continue;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            Profile profile = new Profile(uuid, offlinePlayer);
            profile.load();
            profile.getStats().setDivision(DivisionManager.getInstance().getDivision(profile));
            profiles.put(uuid, profile);
        }
    }

    private UUID parseUuidFromFilename(String filename) {
        String uuidString = filename.substring(0, filename.length() - 4);
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException ignored) {
            Common.sendConsoleMMMessage("<yellow>Warning: Skipping corrupted profile file <white>" + filename + "<yellow> (invalid uuid in filename)");
            return null;
        }
    }

    public void loadAllProfileInformations() {
        Bukkit.getScheduler().runTask(AstralPractice.getInstance(), () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                Profile profile = getProfile(online);
                if (profile != null) {
                    loadProfileInfo(profile);
                }
            }
        });
    }

    public void loadProfileInfo(Profile profile) {
        if (profile == null) {
            return;
        }

        profile.getStats().setDivision(DivisionManager.getInstance().getDivision(profile));

        Player online = profile.getOnlinePlayer();
        if (online != null && online.isOnline() && profile.getSettingsGui() == null) {
            profile.setSettingsGui(new ProfileSettingsGui(profile));
        }
    }

    public void saveProfiles() {
        // Iterate over a stable snapshot to avoid ConcurrentModificationException
        // when autosave overlaps joins/quits/profile updates.
        for (Profile profile : new ArrayList<>(profiles.values())) {
            if (profile != null) {
                profile.save();
            }
        }
    }

    /**
     * Writes every cached profile's statistics to the database in one batch per table.
     *
     * @return a future that completes once both tables are written
     */
    public CompletableFuture<Void> saveProfilesToDatabase() {
        Database database = AstralPractice.getDatabase();
        if (database == null) return CompletableFuture.completedFuture(null);

        List<Profile> present = new ArrayList<>(profiles.values());
        present.removeIf(Objects::isNull);
        if (present.isEmpty()) return CompletableFuture.completedFuture(null);

        return database.getGlobalStatsRepository().saveAll(present)
                .thenCompose(ignored -> database.getLadderStatsRepository().saveAll(present));
    }

    public void saveProfileToDatabase(Profile profile) {
        Database database = AstralPractice.getDatabase();
        if (database == null) return;

        database.getGlobalStatsRepository().save(profile)
                .thenCompose(ignored -> database.getLadderStatsRepository().save(profile));
    }

    public void deleteProfileFromDatabase(UUID uuid) {
        Database database = AstralPractice.getDatabase();
        if (database == null) return;

        database.getGlobalStatsRepository().delete(uuid)
                .thenCompose(ignored -> database.getLadderStatsRepository().delete(uuid))
                .thenCompose(ignored -> database.getMatchHistoryRepository().delete(uuid));
    }

    public void deleteLadderStatsFromDatabase(String ladderName) {
        Database database = AstralPractice.getDatabase();
        if (database == null) return;

        database.getLadderStatsRepository().deleteLadder(ladderName);
    }

    public void demoteOfflineProfile(UUID uuid) {
        if (uuid == null) {
            return;
        }

        Player online = Bukkit.getPlayer(uuid);
        if (online != null && online.isOnline()) {
            return;
        }

        Profile profile = profiles.get(uuid);
        if (profile == null) {
            return;
        }

        profile.save();
        profile.onQuit();
        saveProfileToDatabase(profile);
    }

    public void clearPlayerReference(Player player) {
        if (player == null) {
            return;
        }

        uuids.remove(player);
    }

    private Profile loadProfileIfExists(UUID uuid, OfflinePlayer offlinePlayer) {
        File profileFile = new File(folder, uuid.toString().toLowerCase() + ".yml");
        if (!profileFile.exists()) {
            return null;
        }

        return profiles.computeIfAbsent(uuid, id -> {
            Profile loaded = new Profile(id, offlinePlayer);
            loaded.load();
            loaded.getStats().setDivision(DivisionManager.getInstance().getDivision(loaded));
            return loaded;
        });
    }
}