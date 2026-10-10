package dev.lokspel.practice.manager.ladder.abstraction.interfaces;

import dev.lokspel.practice.manager.fight.match.Match;
import org.bukkit.event.Event;

public interface LadderHandle {

    boolean handleEvents(Event e, Match match);

}
