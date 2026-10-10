package dev.lokspel.practice.manager.nametag;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.fight.util.PlayerUtil;
import dev.lokspel.practice.manager.inventory.InventoryUtil;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import dev.lokspel.practice.util.PAPIUtil;
import dev.lokspel.practice.util.PermanentConfig;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NametagManager {

    private static final String HEALTH_PATH = "MATCH-SETTINGS.HEALTH-BELOW-NAME";
    private static final String LOBBY_PATH = "PLAYER.LOBBY-NAMETAG";
    private static final String FFA_PATH = "FFA.NAMETAG";

    private boolean saturatedHeartIndicator;
    private boolean decimalAlwaysShow;
    private boolean lowHealthRatio;
    private double lowHealthThreshold;
    private double configScale;
    private String healthSymbol;
    private boolean textShadow;
    private int background;
    private String topLine;
    private String bottomLine;
    private String lobbyTopLine;
    private String lobbyBottomLine;
    private boolean lobbyTextShadow;
    private int lobbyBackground;
    private String ffaTopLine;
    private String ffaBottomLine;
    private boolean ffaTextShadow;
    private int ffaBackground;

    private static final String BELOW_NAME_OBJECTIVE = "ZPP_BELOW";

    private static final String HIDE_TEAM_NAME = "zpp_hidden_nametag";
    private static final double VIEW_DISTANCE_SQUARED = 96.0D * 96.0D;
    private static final long REFRESH_INTERVAL_TICKS = 20L;
    private static final long BELOW_NAME_REFRESH_INTERVAL_TICKS = 5L;

    private static NametagManager instance;

    public static NametagManager getInstance() {
        if (instance == null)
            instance = new NametagManager();
        return instance;
    }

    private NametagManager() {
        reloadConfig();
    }

    private final Map<UUID, ClientTextDisplay> displays = new ConcurrentHashMap<>();
    private final Map<UUID, NametagOverride> customNametags = new ConcurrentHashMap<>();
    private final Map<UUID, Component> belowNameLines = new ConcurrentHashMap<>();
    private final java.util.Set<UUID> belowNameUsers = ConcurrentHashMap.newKeySet();

    private Team hideVanillaTeam;
    private NametagDisplayListener listener;
    private BukkitTask refreshTask;
    private BukkitTask belowNameRefreshTask;

    public void initialize() {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            startBelowNameRefreshTask();
            return;
        }

        TeamPacketBlocker.getInstance().register();

        for (Player online : Bukkit.getOnlinePlayers()) {
            hideVanillaNametag(online);
            displays.computeIfAbsent(online.getUniqueId(), ignored -> createDisplay(online));
        }

        if (listener == null) {
            listener = new NametagDisplayListener();
            Bukkit.getPluginManager().registerEvents(listener, AstralPractice.getInstance());
        }

        startRefreshTask();
        startBelowNameRefreshTask();

        for (Player online : Bukkit.getOnlinePlayers()) {
            updateNametag(online);
            refreshViewer(online);
        }
    }

    /**
     * Re-reads the nametag config sections. Call this after a {@code /zpa reload}
     * so lines, colors and backgrounds reflect the latest values.
     */
    public void reload() {
        reloadConfig();

        // Nametag styles and below-name health live in packet metadata, so every
        // online player needs a fresh copy pushed to their client.
        refreshAllNametags();
        updateBelowNameHealthLines();
    }

    private void reloadConfig() {
        saturatedHeartIndicator = ConfigManager.getBoolean(HEALTH_PATH + ".SATURATED-HEART-INDICATOR");
        decimalAlwaysShow = ConfigManager.getBoolean(HEALTH_PATH + ".DECIMAL-HEART-INDICATOR.ALWAYS-SHOW");
        lowHealthRatio = ConfigManager.getBoolean(HEALTH_PATH + ".DECIMAL-HEART-INDICATOR.LOW-HEALTH-DECIMAL-RATIO");
        lowHealthThreshold = ConfigManager.getDouble(HEALTH_PATH + ".LOW-HEALTH-THRESHOLD") * 2.0;
        configScale = ConfigManager.getDouble(HEALTH_PATH + ".SCALE");
        healthSymbol = ConfigManager.getString(HEALTH_PATH + ".SYMBOL");
        textShadow = ConfigManager.getBoolean(HEALTH_PATH + ".TEXT-SHADOW");
        background = ConfigManager.getInt(HEALTH_PATH + ".BACKGROUND");
        topLine = ConfigManager.getString(HEALTH_PATH + ".TOP-LINE");
        bottomLine = ConfigManager.getString(HEALTH_PATH + ".BOTTOM-LINE");
        lobbyTopLine = ConfigManager.getString(LOBBY_PATH + ".TOP-LINE");
        lobbyBottomLine = ConfigManager.getString(LOBBY_PATH + ".BOTTOM-LINE");
        lobbyTextShadow = ConfigManager.getBoolean(LOBBY_PATH + ".TEXT-SHADOW");
        lobbyBackground = ConfigManager.getInt(LOBBY_PATH + ".BACKGROUND");
        ffaTopLine = ConfigManager.getString(FFA_PATH + ".TOP-LINE");
        ffaBottomLine = ConfigManager.getString(FFA_PATH + ".BOTTOM-LINE");
        ffaTextShadow = ConfigManager.getBoolean(FFA_PATH + ".TEXT-SHADOW");
        ffaBackground = ConfigManager.getInt(FFA_PATH + ".BACKGROUND");
    }

    public void shutdown() {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            stopBelowNameRefreshTask();
            belowNameUsers.clear();
            belowNameLines.clear();
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            onPlayerQuit(player);
        }

        if (listener != null) {
            HandlerList.unregisterAll(listener);
            listener = null;
        }

        stopRefreshTask();
        stopBelowNameRefreshTask();

        displays.clear();
        customNametags.clear();
        belowNameLines.clear();
        belowNameUsers.clear();

        TeamPacketBlocker.getInstance().unregister();
    }

    public void reset(String player) {
        Player online = Bukkit.getPlayerExact(player);

        if (online == null) {
            for (Map.Entry<UUID, NametagOverride> entry : customNametags.entrySet()) {
                String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();

                if (player.equalsIgnoreCase(name)) {
                    UUID uuid = entry.getKey();

                    customNametags.remove(uuid);
                    belowNameLines.remove(uuid);
                    belowNameUsers.remove(uuid);
                    break;
                }
            }
            return;
        }

        UUID uuid = online.getUniqueId();

        customNametags.remove(uuid);
        belowNameLines.remove(uuid);
        belowNameUsers.remove(uuid);

        updateNametag(online);
    }

    public void setNametag(Player player, Component prefix, NamedTextColor namedTextColor, Component suffix, int sortPriority) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            return;
        }

        customNametags.put(player.getUniqueId(), new NametagOverride(prefix, namedTextColor, suffix));
        updateNametag(player);
        preserveTabListName(player);
    }

    public void updateNametag(Player player) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED || player == null || !player.isOnline()) {
            return;
        }

        hideVanillaNametag(player);

        ClientTextDisplay display = displays.computeIfAbsent(player.getUniqueId(), ignored -> createDisplay(player));
        applyNametagStyle(display, player);
        display.setText(buildNametagComponent(player));

        preserveTabListName(player);

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            refreshForViewer(viewer, player, true);
        }
    }

    public void sendTeams(Player player) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED || player == null || !player.isOnline()) {
            return;
        }

        onPlayerJoin(player);
    }

    public void onPlayerJoin(Player player) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED || player == null) {
            return;
        }

        hideVanillaNametag(player);
        displays.computeIfAbsent(player.getUniqueId(), ignored -> createDisplay(player));

        updateNametag(player);
        refreshViewer(player);
    }

    public void onPlayerQuit(Player player) {
        if (player == null) {
            return;
        }

        if (hideVanillaTeam != null) {
            hideVanillaTeam.removeEntry(player.getName());
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = viewer.getScoreboard();
            Team team = scoreboard.getTeam(HIDE_TEAM_NAME);
            if (team != null) {
                team.removeEntry(player.getName());
            }
        }

        customNametags.remove(player.getUniqueId());
        belowNameLines.remove(player.getUniqueId());
        belowNameUsers.remove(player.getUniqueId());

        ClientTextDisplay removed = displays.remove(player.getUniqueId());
        if (removed != null) {
            for (UUID viewerUuid : removed.getViewers()) {
                Player viewer = Bukkit.getPlayer(viewerUuid);
                if (viewer != null && viewer.isOnline()) {
                    Packet.sendSetPassengers(viewer, player.getEntityId(), getLivePassengerIds(player));
                    Packet.sendDestroyTextDisplay(viewer, removed.getEntityId());
                }
            }
        }

        for (ClientTextDisplay display : displays.values()) {
            display.removeViewer(player.getUniqueId());
        }
    }

    public void onPlayerMove(Player player) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED || player == null || !player.isOnline()) {
            return;
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            refreshForViewer(viewer, player, false);
        }
        refreshViewer(player);
    }

    public void onVisibilityStateChange(Player player) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED || player == null || !player.isOnline()) {
            return;
        }

        hideVanillaNametag(player);
        refreshViewer(player);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            refreshForViewer(viewer, player, true);
        }
    }

    public void refreshAllNametags() {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            hideVanillaNametag(target);

            ClientTextDisplay display = displays.computeIfAbsent(
                    target.getUniqueId(),
                    ignored -> createDisplay(target)
            );

            applyNametagStyle(display, target);
            display.setText(buildNametagComponent(target));
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                refreshForViewer(viewer, target, true);
            }
        }
    }

    private void refreshViewer(Player viewer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            refreshForViewer(viewer, target, false);
        }
    }

    private void refreshForViewer(Player viewer, Player target, boolean pushMetadata) {
        ClientTextDisplay display = displays.computeIfAbsent(target.getUniqueId(), ignored -> createDisplay(target));
        boolean shouldDisplay = shouldDisplayTo(viewer, target);

        if (!shouldDisplay) {
            if (display.isViewing(viewer.getUniqueId())) {
                Packet.sendSetPassengers(viewer, target.getEntityId(), getLivePassengerIds(target));
                Packet.sendDestroyTextDisplay(viewer, display.getEntityId());
                display.removeViewer(viewer.getUniqueId());
            }
            return;
        }

        boolean newlyVisible = !display.isViewing(viewer.getUniqueId());
        if (newlyVisible) {
            display.addViewer(viewer.getUniqueId());
            Packet.sendSpawnTextDisplay(viewer, display.getEntityId(), display.getEntityUuid(), display.getSpawnLocation(target));
            Packet.sendMetadataTextDisplay(viewer, display);
            Packet.sendSetPassengers(viewer, target.getEntityId(), getLivePassengerIdsWithDisplay(target, display.getEntityId()));
            return;
        }

        if (pushMetadata) {
            Packet.sendMetadataTextDisplay(viewer, display);
            Packet.sendSetPassengers(viewer, target.getEntityId(), getLivePassengerIdsWithDisplay(target, display.getEntityId()));
        }
    }

    private int[] getLivePassengerIds(Player target) {
        List<Integer> passengerIds = new ArrayList<>();
        target.getPassengers().forEach(entity -> passengerIds.add(entity.getEntityId()));
        return passengerIds.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] getLivePassengerIdsWithDisplay(Player target, int displayEntityId) {
        List<Integer> passengerIds = new ArrayList<>();
        target.getPassengers().forEach(entity -> passengerIds.add(entity.getEntityId()));
        passengerIds.add(displayEntityId);
        return passengerIds.stream().mapToInt(Integer::intValue).toArray();
    }

    private boolean shouldDisplayTo(Player viewer, Player target) {
        if (viewer == null || target == null || !viewer.isOnline() || !target.isOnline()) {
            return false;
        }

        if (viewer.isDead()) {
            return false;
        }

        if (viewer.equals(target)) {
            return false;
        }

        if (viewer.getWorld() != target.getWorld()) {
            return false;
        }

        if (!viewer.canSee(target)) {
            return false;
        }

        if (viewer.getLocation().distanceSquared(target.getLocation()) > VIEW_DISTANCE_SQUARED) {
            return false;
        }

        return isTargetVisible(target);
    }

    private boolean isTargetVisible(Player target) {
        if (target.isDead()) {
            return false;
        }


        if (target.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }

        return !target.isInvisible();
    }

    public void setBelowNameLine(Player player, Component line) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED || player == null || !player.isOnline()) {
            return;
        }

        if (line == null || line.equals(Component.empty())) {
            belowNameLines.remove(player.getUniqueId());
        } else {
            belowNameLines.put(player.getUniqueId(), line);
        }

        updateNametag(player);
    }

    public void clearBelowNameLine(Player player) {
        if (player == null) {
            return;
        }

        belowNameLines.remove(player.getUniqueId());
        updateNametag(player);
    }

    private Component buildNametagComponent(Player player) {
        Profile profile = ProfileManager.getInstance().getProfile(player);
        InventoryUtil.LobbyNametag listing = profile != null
                ? InventoryUtil.getLobbyNametag(profile, player.getName(), player)
                : null;

        Component basePrefix = listing != null ? listing.getPrefix() : Component.empty();
        Component baseName = listing != null
                ? listing.getName()
                : Component.text(player.getName(), NamedTextColor.GRAY);
        Component baseSuffix = listing != null ? listing.getSuffix() : Component.empty();

        NametagOverride override = customNametags.get(player.getUniqueId());

        Component composed;
        if (override == null) {
            composed = basePrefix.append(baseName).append(baseSuffix);
        } else {
            Component prefix = override.prefix() != null ? override.prefix() : basePrefix;
            Component suffix = override.suffix() != null ? override.suffix() : baseSuffix;

            Component name = baseName;
            if (override.nameColor() != null) {
                name = name.color(override.nameColor());
            }

            composed = prefix.append(name).append(suffix);
        }

        if (belowNameUsers.contains(player.getUniqueId())) {
            composed = applyNametagLines(player, composed, topLine, bottomLine);
        }

        if (profile != null && (profile.getStatus() == ProfileStatus.LOBBY || profile.getStatus() == ProfileStatus.STAFF_MODE)) {
            composed = applyNametagLines(player, composed, lobbyTopLine, lobbyBottomLine);
        } else if (profile != null && profile.getStatus() == ProfileStatus.FFA) {
            composed = applyNametagLines(player, composed, ffaTopLine, ffaBottomLine);
        }

        Component belowLine = belowNameLines.get(player.getUniqueId());
        return belowLine == null || belowLine.equals(Component.empty())
                ? composed
                : composed.append(Component.newline()).append(belowLine);
    }

    public void initForUser(Player player) {
        if (player == null) {
            return;
        }

        if (PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            belowNameUsers.add(player.getUniqueId());
            if (player.isOnline()) {
                setBelowNameLine(player, formatHealth(player, PlayerUtil.getPlayerHealth(player)));
            }
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Objective objective = scoreboard.getObjective(BELOW_NAME_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(BELOW_NAME_OBJECTIVE, Criteria.DUMMY, Component.empty(), RenderType.INTEGER);
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
    }

    public void cleanUpForUser(Player player) {
        if (player == null) {
            return;
        }

        if (PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            belowNameUsers.remove(player.getUniqueId());
            clearBelowNameLine(player);
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(BELOW_NAME_OBJECTIVE);
        if (objective != null) {
            objective.unregister();
        }
    }

    private void updateBelowNameHealthLines() {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = player.getScoreboard();
                Objective objective = scoreboard.getObjective(BELOW_NAME_OBJECTIVE);

                if (objective == null) {
                    continue;
                }

                for (Player otherPlayer : player.getWorld().getPlayers()) {
                    double health = PlayerUtil.getPlayerHealth(otherPlayer);
                    int hp = (int) Math.ceil(health);

                    Score score = objective.getScore(otherPlayer.getName());
                    score.setScore(hp);
                    score.customName(null);
                    score.numberFormat(NumberFormat.fixed(formatHealth(otherPlayer, health)));
                }
            }
            return;
        }

        for (UUID uuid : belowNameUsers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                belowNameUsers.remove(uuid);
                continue;
            }

            setBelowNameLine(player, formatHealth(player, PlayerUtil.getPlayerHealth(player)));
        }
    }

    private Component formatHealth(Player player, double health) {
        NamedTextColor heartColor = NamedTextColor.RED;
        if (saturatedHeartIndicator && isSaturated(player)) {
            heartColor = NamedTextColor.YELLOW;
        }

        double scale = Math.clamp(configScale == 0 ? 20.0 : configScale, 10.0, 100.0);
        double maxHealth = java.util.Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue();
        double displayHealth = (health / maxHealth) * scale;

        if (decimalAlwaysShow || (lowHealthRatio && health < lowHealthThreshold)) {
            return Component.text(String.format(java.util.Locale.US, "%.1f", displayHealth), NamedTextColor.WHITE)
                    .append(Component.text(healthSymbol, heartColor));
        }

        return Component.text((int) Math.ceil(displayHealth), NamedTextColor.WHITE)
                .append(Component.text(healthSymbol, heartColor));
    }

    private boolean isSaturated(Player player) {
        return player.hasPotionEffect(PotionEffectType.SATURATION) || (player.getFoodLevel() >= 20 && player.getSaturation() > 0);
    }

    private Component applyNametagLines(Player player, Component nametag, String topLine, String bottomLine) {
        Component top = PAPIUtil.runThroughFormat(player, topLine);
        Component bottom = PAPIUtil.runThroughFormat(player, bottomLine);
        if (!top.equals(Component.empty())) {
            nametag = top.append(Component.newline()).append(nametag);
        }
        return bottom.equals(Component.empty()) ? nametag : nametag.append(Component.newline()).append(bottom);
    }

    private void applyNametagStyle(ClientTextDisplay display, Player player) {
        if (belowNameUsers.contains(player.getUniqueId())) {
            display.setTextShadow(textShadow);
            display.setBackground(background);
            return;
        }

        Profile profile = ProfileManager.getInstance().getProfile(player);
        if (profile != null && profile.getStatus() == ProfileStatus.FFA) {
            display.setTextShadow(ffaTextShadow);
            display.setBackground(ffaBackground);
        } else if (profile != null && (profile.getStatus() == ProfileStatus.LOBBY || profile.getStatus() == ProfileStatus.STAFF_MODE)) {
            display.setTextShadow(lobbyTextShadow);
            display.setBackground(lobbyBackground);
        } else {
            display.setTextShadow(false);
            display.setBackground(0);
        }
    }

    private ClientTextDisplay createDisplay(Player owner) {
        ClientTextDisplay display = new ClientTextDisplay(owner);
        display.setTextAlignmentCenter();
        display.setTextShadow(false);
        display.setSeeThrough(false);
        display.setBackground(0);
        return display;
    }

    private void ensureHideTeam() {
        if (Bukkit.getScoreboardManager() == null) {
            return;
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = viewer.getScoreboard();
            Team team = scoreboard.getTeam(HIDE_TEAM_NAME);
            if (team == null) {
                team = scoreboard.registerNewTeam(HIDE_TEAM_NAME);
            }

            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setCanSeeFriendlyInvisibles(false);
            team.addEntry(viewer.getName());
        }

        hideVanillaTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(HIDE_TEAM_NAME);
        if (hideVanillaTeam == null) {
            hideVanillaTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(HIDE_TEAM_NAME);
        }
        hideVanillaTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        hideVanillaTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        hideVanillaTeam.setCanSeeFriendlyInvisibles(false);
    }

    private void addToHideTeam(Player player) {
        if (Bukkit.getScoreboardManager() == null || player == null) {
            return;
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = viewer.getScoreboard();
            Team team = scoreboard.getTeam(HIDE_TEAM_NAME);
            if (team == null) {
                team = scoreboard.registerNewTeam(HIDE_TEAM_NAME);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                team.setCanSeeFriendlyInvisibles(false);
            }
            team.addEntry(player.getName());
        }

        if (hideVanillaTeam != null) {
            hideVanillaTeam.addEntry(player.getName());
        }
    }

    public void preserveTabListName(Player player) {
        try {
            Profile profile = ProfileManager.getInstance().getProfile(player);
            if (profile == null) {
                return;
            }

            InventoryUtil.LobbyNametag lobbyNametag = InventoryUtil.getLobbyNametag(profile, player.getName(), player);

            NametagOverride override = customNametags.get(player.getUniqueId());
            Component prefix = override != null && override.prefix() != null ? override.prefix() : lobbyNametag.getPrefix();
            Component name = lobbyNametag.getName();
            if (override != null && override.nameColor() != null) {
                name = name.color(override.nameColor());
            }
            Component suffix = override != null && override.suffix() != null ? override.suffix() : lobbyNametag.getSuffix();

            Component tabListName = prefix.append(name).append(suffix);
            TabIntegration tabIntegration = TeamPacketBlocker.getInstance().getTabIntegration();
            if (tabIntegration == null || !tabIntegration.setTabListName(player, prefix, name, suffix)) {
                PlayerUtil.setPlayerListName(player, tabListName);
            }
        } catch (Exception ignored) {
        }
    }

    public Component getTabListName(Player player) {
        Profile profile = ProfileManager.getInstance().getProfile(player);
        if (profile == null) {
            return Component.text(player.getName(), NamedTextColor.GRAY);
        }

        InventoryUtil.LobbyNametag lobbyNametag =
                InventoryUtil.getLobbyNametag(profile, player.getName(), player);

        NametagOverride override = customNametags.get(player.getUniqueId());

        Component prefix = override != null && override.prefix() != null
                ? override.prefix()
                : lobbyNametag.getPrefix();

        Component name = lobbyNametag.getName();

        if (override != null && override.nameColor() != null) {
            name = name.color(override.nameColor());
        }

        Component suffix = override != null && override.suffix() != null
                ? override.suffix()
                : lobbyNametag.getSuffix();

        return prefix.append(name).append(suffix);
    }

    private void startRefreshTask() {
        stopRefreshTask();
        refreshTask = Bukkit.getScheduler().runTaskTimer(
                AstralPractice.getInstance(),
                this::refreshAllNametags,
                REFRESH_INTERVAL_TICKS,
                REFRESH_INTERVAL_TICKS
        );
    }

    private void startBelowNameRefreshTask() {
        stopBelowNameRefreshTask();
        belowNameRefreshTask = Bukkit.getScheduler().runTaskTimer(
                AstralPractice.getInstance(),
                this::updateBelowNameHealthLines,
                0L,
                BELOW_NAME_REFRESH_INTERVAL_TICKS
        );
    }

    private void stopRefreshTask() {
        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }
    }

    private void stopBelowNameRefreshTask() {
        if (belowNameRefreshTask != null) {
            belowNameRefreshTask.cancel();
            belowNameRefreshTask = null;
        }
    }

    private void hideVanillaNametag(Player player) {
        if (!PermanentConfig.NAMETAG_MANAGEMENT_ENABLED) {
            return;
        }

        if (player == null) {
            return;
        }

        TabIntegration tabIntegration = TeamPacketBlocker.getInstance().getTabIntegration();
        if (tabIntegration != null && tabIntegration.isAvailable()) {
            tabIntegration.hideNametag(player);
            return;
        }

        ensureHideTeam();
        addToHideTeam(player);
    }

    private record NametagOverride(Component prefix, NamedTextColor nameColor, Component suffix) {
    }
}
