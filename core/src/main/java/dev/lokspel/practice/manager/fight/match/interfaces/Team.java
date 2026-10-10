package dev.lokspel.practice.manager.fight.match.interfaces;

import dev.lokspel.practice.manager.fight.match.enums.TeamEnum;
import org.bukkit.entity.Player;

public interface Team {

    TeamEnum getTeam(Player player);

}
