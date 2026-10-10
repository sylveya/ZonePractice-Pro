package dev.lokspel.practice.manager.fight.event.events.duel.interfaces;

import dev.lokspel.practice.manager.fight.event.enums.EventType;
import dev.lokspel.practice.manager.fight.event.interfaces.EventData;
import org.bukkit.Location;

public abstract class DuelEventData extends EventData {

    public DuelEventData(EventType type) {
        super(type);
    }

    public Location getLocation1() {
        return spawns.get(0);
    }

    public Location getLocation2() {
        return spawns.get(1);
    }

}
