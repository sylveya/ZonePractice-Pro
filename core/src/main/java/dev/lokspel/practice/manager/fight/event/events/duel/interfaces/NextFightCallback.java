package dev.lokspel.practice.manager.fight.event.events.duel.interfaces;

import org.bukkit.entity.Player;

public interface NextFightCallback {

    void onNextFight(Player leftPlayer);

}
