package dev.lokspel.practice.manager.matchhistory;

import dev.lokspel.practice.manager.backend.ConfigFile;
import dev.lokspel.practice.util.Common;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * YAML-backed per-player match history stored at:
 *   plugins/AstralPractice/match-history/<uuid>.yml
 *
 * Stores up to 5 recent match entries per player.
 */
public class MatchHistoryFile extends ConfigFile {

    private static final int MAX_HISTORY = 5;
    private static final String ROOT = "matches";

    private final MatchHistory matchHistory;

    public MatchHistoryFile(MatchHistory matchHistory) {
        super("/match-history/", matchHistory.getUuid().toString().toLowerCase());
        this.matchHistory = matchHistory;

        saveFile();
        reloadFile();
    }

    @Override
    public void setData() {
        saveFile();
    }

    /**
     * Saves a new match and prunes entries beyond the cap.
     * Returns the assigned id.
     */
    public int saveMatch(MatchHistoryEntry entry) {
        int nextId = getNextId();
        setMatch(entry, nextId);
        pruneOldMatches();
        saveFile();
        return nextId;
    }

    private void setMatch(MatchHistoryEntry entry, int matchId) {
        String match = ROOT + "." + matchId;

        config.set(match + ".uuid",                entry.getPlayerUuid().toString());
        config.set(match + ".opponentUuid",        entry.getOpponentUuid().toString());
        config.set(match + ".username",            entry.getPlayerName());
        config.set(match + ".opponentName",        entry.getOpponentName());
        config.set(match + ".kitName",             entry.getKitName());
        config.set(match + ".arenaName",           entry.getArenaName());
        config.set(match + ".score",               entry.getPlayerScore());
        config.set(match + ".opponentScore",       entry.getOpponentScore());
        config.set(match + ".finalHealth",         entry.getPlayerFinalHealth());
        config.set(match + ".opponentFinalHealth", entry.getOpponentFinalHealth());
        config.set(match + ".winnerUuid",          entry.getWinnerUuid() != null ? entry.getWinnerUuid().toString() : "");
        config.set(match + ".matchDuration",       entry.getMatchDuration());
        config.set(match + ".playedAt",            entry.getPlayedAt());
    }

    @Override
    public void getData() {
        loadMatches();
    }

    private void loadMatches() {
        matchHistory.getMatches().clear();

        ConfigurationSection root = config.getConfigurationSection(ROOT);
        if (root == null) return;

        for (String key : root.getKeys(false)) {
            MatchHistoryEntry entry = loadMatch(key);
            if (entry != null) {
                matchHistory.getMatches().add(entry);
            }
        }

        matchHistory.getMatches().sort((a, b) -> Integer.compare(b.getMatchId(), a.getMatchId()));
    }

    private MatchHistoryEntry loadMatch(String matchId) {
        String match = ROOT + "." + matchId;

        try {
            return new MatchHistoryEntry(
                    Integer.parseInt(matchId),
                    UUID.fromString(config.getString(match + ".uuid",       matchHistory.getUuid().toString())),
                    UUID.fromString(config.getString(match + ".opponentUuid", "00000000-0000-0000-0000-000000000000")),
                    config.getString(match + ".username",         "Unknown"),
                    config.getString(match + ".opponentName",     "Unknown"),
                    config.getString(match + ".kitName",          "Unknown"),
                    config.getString(match + ".arenaName",        "Unknown"),
                    config.getInt(match + ".score",                 0),
                    config.getInt(match + ".opponentScore",         0),
                    config.getDouble(match + ".finalHealth",         0.0),
                    config.getDouble(match + ".opponentFinalHealth", 0.0),
                    getWinnerUuid(match),
                    config.getInt(match + ".matchDuration",       0),
                    config.getLong(match + ".playedAt",          System.currentTimeMillis())
            );
        } catch (Exception e) {
            Common.sendConsoleMMMessage("<yellow>[MatchHistory] Skipping corrupt entry " + matchId
                    + " for " + matchHistory.getUuid() + ": " + e.getMessage());
            return null;
        }
    }

    private UUID getWinnerUuid(String match) {
        String winnerUuid = config.getString(match + ".winnerUuid");
        return (winnerUuid != null && !winnerUuid.isEmpty()) ? UUID.fromString(winnerUuid) : null;
    }

    private int getNextId() {
        ConfigurationSection root = config.getConfigurationSection(ROOT);
        if (root == null) return 1;
        int max = 0;
        for (String key : root.getKeys(false)) {
            try { int id = Integer.parseInt(key); if (id > max) max = id; }
            catch (NumberFormatException ignored) {}
        }
        return max + 1;
    }

    private void pruneOldMatches() {
        ConfigurationSection root = config.getConfigurationSection(ROOT);
        if (root == null) return;

        List<Integer> ids = new ArrayList<>();
        for (String key : root.getKeys(false)) {
            try { ids.add(Integer.parseInt(key)); } catch (NumberFormatException ignored) {}
        }

        if (ids.size() <= MAX_HISTORY) return;

        ids.sort((a, b) -> Integer.compare(b, a)); // newest first
        for (int id : ids.subList(MAX_HISTORY, ids.size())) {
            config.set(ROOT + "." + id, null);
        }
    }
}