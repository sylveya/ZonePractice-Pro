package dev.lokspel.practice.manager.backend.database.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class MatchHistoryRow {

    private int matchId;
    private String uuid;
    private String opponentUuid;
    private String username;
    private String opponentName;
    private String kitName;
    private String arenaName;
    private int score;
    private int opponentScore;
    private double finalHealth;
    private double opponentFinalHealth;
    private String winnerUuid;
    private int matchDuration;
    private long playedAt;

    public MatchHistoryRow(UUID playerUuid, UUID opponentUuid, String playerName, String opponentName,
                           String kitName, String arenaName, int playerScore, int opponentScore,
                           double playerFinalHealth, double opponentFinalHealth, UUID winnerUuid,
                           int matchDuration, long playedAt) {
        this.uuid = playerUuid.toString();
        this.opponentUuid = opponentUuid.toString();
        this.username = playerName;
        this.opponentName = opponentName;
        this.kitName = kitName;
        this.arenaName = arenaName;
        this.score = playerScore;
        this.opponentScore = opponentScore;
        this.finalHealth = playerFinalHealth;
        this.opponentFinalHealth = opponentFinalHealth;
        this.winnerUuid = winnerUuid != null ? winnerUuid.toString() : null;
        this.matchDuration = matchDuration;
        this.playedAt = playedAt;
    }
}
