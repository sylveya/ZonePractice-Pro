package dev.lokspel.api.Interface;

import org.bukkit.entity.Player;

import java.util.List;

public interface Party {

    /**
     * Get the leader
     * @return the leader
     */
    Player getLeader();

    /**
     * Get the members
     * @return the members
     */
    List<Player> getMembers();

}
