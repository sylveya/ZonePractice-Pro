package dev.lokspel.practice.manager.ladder.settings.handlers;

import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.util.ModernItemCooldownHandler;
import dev.lokspel.practice.manager.ladder.settings.SettingHandler;
import dev.lokspel.practice.manager.server.ServerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handler for the GOLDEN_APPLE_COOLDOWN setting.
 * Controls the cooldown time for consuming golden apples.
 * <p>
 * IMPLEMENTATION LOCATION: This replaces the logic in LadderSettingListener.onGoldenHeadConsume()
 */
public class GoldenAppleSettingHandler implements SettingHandler<Double> {

    @Override
    public Double getValue(Match match) {
        return match.getLadder().getGoldenAppleCooldown();
    }

    @Override
    public boolean handleEvent(Event event, Match match, Player player) {
        if (!(event instanceof PlayerItemConsumeEvent e)) {
            return false;
        }

        ItemStack item = e.getItem();
        if (!item.getType().equals(Material.GOLDEN_APPLE)) {
            return false;
        }

        double cooldown = getValue(match);
        if (cooldown < 1) {
            return false; // No cooldown
        }

        // Golden heads have their own consume logic/cooldown.
        if (ServerManager.getInstance()
                .getGoldenHead().isGoldenHead(item)) {
            return false; // Golden heads don't have cooldown
        }

        ModernItemCooldownHandler.handleGoldenApple(player, cooldown, e);

        return e.isCancelled();
    }

}
