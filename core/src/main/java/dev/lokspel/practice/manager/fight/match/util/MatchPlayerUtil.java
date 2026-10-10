package dev.lokspel.practice.manager.fight.match.util;

import dev.lokspel.practice.manager.fight.util.PlayerUtil;
import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.util.entityhider.PlayerHider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public enum MatchPlayerUtil {
    ;

    public static void hidePlayerPartyGames(Player hider, List<Player> matchPlayers) {
        for (Player matchPlayer : matchPlayers) {
            if (!matchPlayer.equals(hider))
                PlayerHider.getInstance().hidePlayer(matchPlayer, hider);
        }

        dev.lokspel.practice.util.playerutil.PlayerUtil.setFightPlayer(hider);

        PlayerUtil.setCollidesWithEntities(hider, false);
        Bukkit.getScheduler().runTaskLater(AstralPractice.getInstance(), () ->
        {
            hider.setAllowFlight(true);
            hider.setFlying(true);
            hider.setFireTicks(0);
        }, 2L);
    }

}
