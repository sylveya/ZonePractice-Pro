package dev.lokspel.practice.manager.backend.database.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LadderStatsRow {

    private int id;
    private String uuid;
    private String username;
    private String ladder;
    private int unrankedWins;
    private int unrankedLosses;
    private int unrankedWinStreak;
    private int unrankedBestWinStreak;
    private int unrankedLoseStreak;
    private int unrankedBestLoseStreak;
    private int rankedWins;
    private int rankedLosses;
    private int rankedWinStreak;
    private int rankedBestWinStreak;
    private int rankedLoseStreak;
    private int rankedBestLoseStreak;
    private int elo;
    private String rank;
    private int kills;
    private int deaths;
}
