package dev.lokspel.practice.manager.ladder.type;

import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.ladder.abstraction.interfaces.LadderHandle;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.ladder.enums.LadderType;
import org.bukkit.event.Event;

public class Basic extends NormalLadder implements LadderHandle {

    public Basic(String name, LadderType type) {
        super(name, type);
    }

    @Override
    public boolean handleEvents(Event e, Match match) {
        return false;
    }

}
