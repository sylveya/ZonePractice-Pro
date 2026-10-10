package dev.lokspel.practice.listener;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ItemConsume implements Listener {

    @EventHandler ( ignoreCancelled = true )
    public void onConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        Profile profile = ProfileManager.getInstance().getProfile(player);
        ItemStack item = e.getItem();

        switch (profile.getStatus()) {
            case MATCH:
            case FFA:
            case EVENT:
                if (item.getType() == Material.POTION && ConfigManager.getConfig().getBoolean("MATCH-SETTINGS.REMOVE-EMPTY-BOTTLE"))
                    Bukkit.getScheduler().runTaskLater(AstralPractice.getInstance(), () -> player.getInventory().remove(Material.GLASS_BOTTLE), 1L);
                break;
        }
    }

}
