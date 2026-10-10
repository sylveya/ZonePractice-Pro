package dev.lokspel.practice.listener;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Prevents players from joining the server until the plugin is fully loaded.
 * Also prevents duplicate username logins.
 */
public class PlayerPreLogin implements Listener {

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        // Check if plugin is fully loaded
        if (!AstralPractice.isFullyLoaded()) {
            String message = LanguageManager.getString("PLUGIN-LOADING-MESSAGE");
            if (message == null || message.isEmpty()) {
                message = "<red>The server is still loading. Please try again in a moment.";
            }
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Common.deserializeMiniMessage(message));
            return;
        }

        // Check for duplicate username (different UUID, same name)
        String joiningPlayerName = event.getName();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(joiningPlayerName)) {
                String message = LanguageManager.getString("DUPLICATE-USERNAME-KICK");
                if (message == null || message.isEmpty()) {
                    message = "<red>A player with your username is already logged in!";
                }
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Common.deserializeMiniMessage(message));
                return;
            }
        }
    }
}
