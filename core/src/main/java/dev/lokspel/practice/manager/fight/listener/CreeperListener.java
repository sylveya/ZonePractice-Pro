package dev.lokspel.practice.manager.fight.listener;

import com.destroystokyo.paper.event.entity.CreeperIgniteEvent;
import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.MatchManager;
import dev.lokspel.practice.manager.fight.match.enums.RoundStatus;
import dev.lokspel.practice.manager.ladder.enums.LadderType;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CreeperListener implements Listener {

    private static final long SCAN_DELAY_TICKS = 20L;
    private static final long SCAN_PERIOD_TICKS = 1L;
    private static final double TRIGGER_DISTANCE = 16.0;

    public CreeperListener() {
        startCreeperScanner();
    }

    private void startCreeperScanner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                scanMatches();
            }
        }.runTaskTimer(ZonePractice.getInstance(), SCAN_DELAY_TICKS, SCAN_PERIOD_TICKS);
    }

    private static void scanMatches() {
        for (Match match : MatchManager.getInstance().getLiveMatches()) {
            if (isCreeperLiveMatch(match)) {
                igniteCreepers(match);
            }
        }
    }

    private static boolean isCreeperLiveMatch(Match match) {
        return match != null
                && match.getLadder().getType() == LadderType.CREEPER
                && match.getCurrentRound().getRoundStatus() == RoundStatus.LIVE;
    }

    private static void igniteCreepers(Match match) {
        int fuseTicks = getFuseTicks(match);

        for (Player player : match.getPlayers()) {
            if (!isAlive(player)) {
                continue;
            }

            for (Entity entity : player.getNearbyEntities(
                    TRIGGER_DISTANCE,
                    TRIGGER_DISTANCE,
                    TRIGGER_DISTANCE
            )) {
                if (entity instanceof Creeper creeper) {
                    ignite(creeper, fuseTicks);
                }
            }
        }
    }

    private static int getFuseTicks(Match match) {
        dev.lokspel.practice.manager.ladder.type.Creeper ladder =
                (dev.lokspel.practice.manager.ladder.type.Creeper) match.getLadder();

        return ladder.getCreeperExplosionDelayTicks();
    }

    private static boolean isAlive(Player player) {
        return player.isValid() && !player.isDead();
    }

    private static void ignite(Creeper creeper, int fuseTicks) {
        if (!isValid(creeper) || creeper.isIgnited()) {
            return;
        }

        creeper.setMaxFuseTicks(fuseTicks);
        creeper.ignite();
    }

    private static boolean isValid(Creeper creeper) {
        return creeper.isValid() && !creeper.isDead();
    }

    @EventHandler
    public void onCreeperDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Creeper victim)
                || !(event.getDamager() instanceof Creeper)) {
            return;
        }

        Match match = getCreeperMatch(victim.getLocation());
        if (!isCreeperLiveMatch(match)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onCreeperIgnite(CreeperIgniteEvent event) {
        Creeper creeper = event.getEntity();

        Match match = getCreeperMatch(creeper.getLocation());
        if (!isCreeperLiveMatch(match)) {
            return;
        }

        int fuseTicks = getFuseTicks(match);

        creeper.setMaxFuseTicks(fuseTicks);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        Match match = getCreeperMatch(creeper.getLocation());
        if (match == null || isCreeperLiveMatch(match)) {
            return;
        }

        event.setCancelled(true);
    }

    private static Match getCreeperMatch(Location location) {
        for (Match match : MatchManager.getInstance().getLiveMatches()) {
            if (match.getLadder().getType() != LadderType.CREEPER) {
                continue;
            }

            if (match.getCuboid() == null || !match.getCuboid().contains(location)) {
                continue;
            }

            return match;
        }

        return null;
    }
}