package dev.lokspel.practice.manager.fight.event.events.ffa.oitc;

import dev.lokspel.practice.manager.fight.event.enums.EventType;
import dev.lokspel.practice.manager.fight.event.interfaces.EventData;

import java.io.IOException;

public class OITCData extends EventData {

    public OITCData() {
        super(EventType.OITC);
    }

    @Override
    protected void setCustomData() {
    }

    @Override
    protected void getCustomData() {
    }

    @Override
    protected void enable() throws IOException {
    }

}
