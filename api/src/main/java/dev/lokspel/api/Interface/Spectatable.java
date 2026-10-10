package dev.lokspel.api.Interface;

import org.bukkit.entity.Player;

import java.util.List;

public interface Spectatable {

    /**
     * Get the spectators of the match/ffa/event.
     * @return The spectators of the match/ffa/event
     */
    List<Player> getSpectators();

    /**
     * Add a spectator to the match/ffa/event.
     * @param spectator The player that is spectating
     * @param target The player that is being spectated
     * @param teleport Whether the spectator should be teleported to the target
     * @param message Whether the start of the spectating should be messaged
     */
    void addSpectator(Player spectator, Player target, boolean teleport, boolean message);

    /**
     * Remove a spectator from the match/ffa/event.
     * @param player The player that is spectating
     */
    void removeSpectator(Player player);

    /**
     * Send a message to the match/ffa/event.
     * @param message The message to send. Must be MM format.
     * @param spectate Whether the message should be sent to spectators
     */
    void sendMessage(String message, boolean spectate);

}
