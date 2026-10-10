package dev.lokspel.practice.manager.backend.database.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GlobalStatsRow {

    private int id;
    private String uuid;
    private String username;
    private long firstJoin;
    private long lastJoin;
    private int unrankedWins;
    private int unrankedLosses;
    private int rankedWins;
    private int rankedLosses;
    private int globalElo;
    private String globalRank;
    private int experience;
    private int winStreak;
    private int bestWinStreak;
    private int loseStreak;
    private int bestLoseStreak;
}
