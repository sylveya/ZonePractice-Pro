package dev.lokspel.practice.manager.fight.listener;

import dev.lokspel.practice.manager.fight.ffa.FFAManager;
import dev.lokspel.practice.manager.fight.ffa.game.FFA;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.MatchManager;
import dev.lokspel.practice.manager.fight.match.enums.RoundStatus;
import io.papermc.paper.event.player.PlayerItemCooldownEvent;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/**
 * Replaces vanilla projectile cooldowns with configured cooldowns.
 */
public class ProjectileCooldownListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnderPearlCooldownSet(PlayerItemCooldownEvent e) {
        if (e.getType() != Material.ENDER_PEARL) {
            return;
        }

        Player player = e.getPlayer();

        Match match = MatchManager.getInstance().getLiveMatchByPlayer(player);
        if (match != null) {
            if (!match.getCurrentRound().getRoundStatus().equals(RoundStatus.LIVE)) {
                e.setCancelled(true);
                return;
            }

            double duration = match.getLadder().getEnderPearlCooldown();
            if (duration <= 0) {
                e.setCancelled(true);
            } else {
                e.setCooldown((int) (duration * 20));
            }
            return;
        }

        FFA ffa = FFAManager.getInstance().getFFAByPlayer(player);
        if (ffa != null && ffa.getPlayers().containsKey(player)) {
            double duration = ffa.getPlayers().get(player).getEnderPearlCooldown();
            if (duration <= 0) {
                e.setCancelled(true);
            } else {
                e.setCooldown((int) (duration * 20));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWindChargeCooldownSet(PlayerItemCooldownEvent e) {
        if (e.getType() != Material.WIND_CHARGE) {
            return;
        }

        Player player = e.getPlayer();
        Match match = MatchManager.getInstance().getLiveMatchByPlayer(player);
        if (match == null) {
            return;
        }

        double duration = match.getLadder().getWindChargeCooldown();
        if (duration <= 0) {
            e.setCancelled(true);
        } else {
            e.setCooldown((int) (duration * 20));
        }
    }

    @EventHandler
    public void onProjectileShoot(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EnderPearl)) {
            return;
        }

        if (!(e.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        FFA ffa = FFAManager.getInstance().getFFAByPlayer(player);
        if (ffa != null) {
            return;
        }

        Match match = MatchManager.getInstance().getLiveMatchByPlayer(player);
        if (match != null) {
            if (!match.getCurrentRound().getRoundStatus().equals(RoundStatus.LIVE)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onWindChargeShoot(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof WindCharge)) {
            return;
        }

        if (!(e.getEntity().getShooter() instanceof Player windPlayer)) {
            return;
        }

        Match windMatch = MatchManager.getInstance().getLiveMatchByPlayer(windPlayer);
        if (windMatch == null) {
            return;
        }

        double duration = windMatch.getLadder().getWindChargeCooldown();
        if (duration <= 0) {
            return;
        }

        if (!windMatch.getCurrentRound().getRoundStatus().equals(RoundStatus.LIVE)) {
            e.setCancelled(true);
        }
    }

}
