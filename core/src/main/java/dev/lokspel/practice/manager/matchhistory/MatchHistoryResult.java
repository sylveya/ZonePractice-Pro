package dev.lokspel.practice.manager.matchhistory;

import dev.lokspel.practice.manager.backend.database.model.MatchHistoryRow;
import lombok.Getter;

import java.util.UUID;

/**
 * A finished match, recorded once and then viewed from either side. The YAML entry,
 * the database row and the cached entry are all derived from this one value.
 */
@Getter
public final class MatchHistoryResult {

    private final UUID playerUuid;
    private final UUID opponentUuid;
    private final String playerName;
    private final String opponentName;
    private final String kitName;
    private final String arenaName;
    private final int playerScore;
    private final int opponentScore;
    private final double playerFinalHealth;
    private final double opponentFinalHealth;
    private final UUID winnerUuid;
    private final int matchDuration;
    private final long playedAt;

    public MatchHistoryResult(UUID playerUuid, UUID opponentUuid,
                              String playerName, String opponentName,
                              String kitName, String arenaName,
                              int playerScore, int opponentScore,
                              double playerFinalHealth, double opponentFinalHealth,
                              UUID winnerUuid, int matchDuration, long playedAt) {
        this.playerUuid = playerUuid;
        this.opponentUuid = opponentUuid;
        this.playerName = playerName;
        this.opponentName = opponentName;
        this.kitName = kitName;
        this.arenaName = arenaName;
        this.playerScore = playerScore;
        this.opponentScore = opponentScore;
        this.playerFinalHealth = playerFinalHealth;
        this.opponentFinalHealth = opponentFinalHealth;
        this.winnerUuid = winnerUuid;
        this.matchDuration = matchDuration;
        this.playedAt = playedAt;
    }

    public MatchHistoryResult fromOpponent() {
        return new MatchHistoryResult(
                opponentUuid, playerUuid,
                opponentName, playerName,
                kitName, arenaName,
                opponentScore, playerScore,
                opponentFinalHealth, playerFinalHealth,
                winnerUuid, matchDuration, playedAt
        );
    }

    /** @param matchId the id the YAML store assigns, or {@code -1} before one exists */
    public MatchHistoryEntry toEntry(int matchId) {
        return new MatchHistoryEntry(
                matchId,
                playerUuid, opponentUuid,
                playerName, opponentName,
                kitName, arenaName,
                playerScore, opponentScore,
                playerFinalHealth, opponentFinalHealth,
                winnerUuid, matchDuration, playedAt
        );
    }

    public MatchHistoryRow toRow() {
        return new MatchHistoryRow(
                playerUuid, opponentUuid,
                playerName, opponentName,
                kitName, arenaName,
                playerScore, opponentScore,
                playerFinalHealth, opponentFinalHealth,
                winnerUuid, matchDuration, playedAt
        );
    }
}
