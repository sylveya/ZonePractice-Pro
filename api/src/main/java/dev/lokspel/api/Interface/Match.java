package dev.lokspel.api.Interface;

import org.bukkit.entity.Player;

import java.util.List;

public interface Match extends Spectatable {

    /**
     * Get the players of the match.
     * @return The players of the match
     */
    List<Player> getPlayers();

    /**
     * Get the spectators of the match.
     * @return The spectators of the match
     */
    List<Player> getSpectators();

    /**
     * Get the amount of wins needed to win the match.
     * @return The amount of wins needed to win the match
     */
    int getWinsNeeded();

    /**
     * Get the ladder name of the match.
     * @return The ladder name of the match, or null if not set
     */
    String getLadderName();

    /**
     * Get the ladder display name of the match.
     * @return The ladder display name of the match, or null if not set
     */
    String getLadderDisplayName();

}
