package dev.lokspel.practice.manager.matchhistory;

import dev.lokspel.api.Event.Match.MatchEndEvent;
import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.database.Database;
import dev.lokspel.practice.manager.fight.match.type.duel.Duel;
import dev.lokspel.practice.manager.fight.util.Stats.Statistic;
import dev.lokspel.practice.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MatchHistoryManager implements Listener {

    private static final int MAX_HISTORY = 5;

    private static MatchHistoryManager instance;

    public static MatchHistoryManager getInstance() {
        if (instance == null) instance = new MatchHistoryManager();
        return instance;
    }

    private final Map<UUID, MatchHistory> matchHistories = new ConcurrentHashMap<>();

    private MatchHistoryManager() {
        Bukkit.getPluginManager().registerEvents(this, AstralPractice.getInstance());
    }

    public MatchHistory getMatchHistory(UUID uuid) {
        return matchHistories.computeIfAbsent(uuid, MatchHistory::new);
    }

    public void saveMatch(MatchHistoryResult result) {
        CompletableFuture.runAsync(() -> {
            MatchHistoryResult opponentView = result.fromOpponent();

            // The YAML store hands out the id, and both players share it so the two
            // entries stay recognisably the same match.
            int matchId = saveToYaml(result.getPlayerUuid(), result);
            saveToYaml(opponentView.getPlayerUuid(), opponentView);

            getMatchHistory(result.getPlayerUuid()).add(result.toEntry(matchId));
            getMatchHistory(opponentView.getPlayerUuid()).add(opponentView.toEntry(matchId));

            saveToDatabase(result, opponentView);
        });
    }

    public CompletableFuture<List<MatchHistoryEntry>> loadHistoryAsync(UUID playerUuid) {
        MatchHistory history = matchHistories.get(playerUuid);
        if (history != null && !history.getMatches().isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>(history.getMatches()));
        }

        return CompletableFuture.supplyAsync(() -> getMatchHistory(playerUuid).load())
                .thenCompose(entries -> entries.isEmpty() ? loadFromDatabase(playerUuid) : CompletableFuture.completedFuture(entries));
    }

    private CompletableFuture<List<MatchHistoryEntry>> loadFromDatabase(UUID playerUuid) {
        Database database = AstralPractice.getDatabase();
        if (database == null) return CompletableFuture.completedFuture(List.of());

        return database.getMatchHistoryRepository().getHistory(playerUuid, MAX_HISTORY)
                .thenApply(entries -> {
                    getMatchHistory(playerUuid).getMatches().addAll(entries);
                    return entries;
                });
    }

    private void saveToDatabase(MatchHistoryResult result, MatchHistoryResult opponentView) {
        Database database = AstralPractice.getDatabase();
        if (database == null) return;

        database.getMatchHistoryRepository().save(result.toRow(), opponentView.toRow(), MAX_HISTORY)
                .exceptionally(error -> {
                    Common.sendConsoleMMMessage("<red>[MatchHistory] database save error: " + error.getMessage());
                    return null;
                });
    }

    private int saveToYaml(UUID uuid, MatchHistoryResult result) {
        try {
            return getMatchHistory(uuid).getFile().saveMatch(result.toEntry(-1));
        } catch (Exception e) {
            Common.sendConsoleMMMessage("<red>[MatchHistory] YAML save error for " + uuid + ": " + e.getMessage());
            return -1;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMatchEnd(MatchEndEvent e) {
        if (!(e.getMatch() instanceof Duel duel)) {
            return;
        }

        Player player1 = duel.getPlayer1();
        Player player2 = duel.getPlayer2();
        if (player1 == null || player2 == null) {
            return;
        }

        Map<UUID, Statistic> statistics = duel.getCurrentRound().getStatistics();
        Statistic stat1 = statistics.get(player1.getUniqueId());
        Statistic stat2 = statistics.get(player2.getUniqueId());

        double p1Health = (stat1 != null && stat1.isSet())
                ? stat1.getEndHeart()
                : (player1.isOnline() ? player1.getHealth() : 0.0);

        double p2Health = (stat2 != null && stat2.isSet())
                ? stat2.getEndHeart()
                : (player2.isOnline() ? player2.getHealth() : 0.0);

        UUID winnerUuid = duel.getMatchWinner() != null ? duel.getMatchWinner().getUniqueId() : null;

        saveMatch(new MatchHistoryResult(
                player1.getUniqueId(), player2.getUniqueId(),
                player1.getName(), player2.getName(),
                duel.getLadder().getName(), duel.getArena().getName(),
                duel.getWonRounds(player1), duel.getWonRounds(player2),
                p1Health, p2Health,
                winnerUuid,
                duel.getDuration(),
                System.currentTimeMillis()
        ));
    }
}
