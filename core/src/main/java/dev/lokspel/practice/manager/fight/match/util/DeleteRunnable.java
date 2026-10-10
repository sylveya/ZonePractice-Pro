package dev.lokspel.practice.manager.fight.match.util;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.MatchManager;
import org.bukkit.Bukkit;

public enum DeleteRunnable {
    ;

    public static void start(Match match) {
        Bukkit.getScheduler().runTaskLater(ZonePractice.getInstance(), () ->
                        MatchManager.getInstance().getMatches().remove(match.getId()),
                20L * ConfigManager.getInt("MATCH-SETTINGS.MATCH-STATISTIC.REMOVE-AFTER"));
    }

}
