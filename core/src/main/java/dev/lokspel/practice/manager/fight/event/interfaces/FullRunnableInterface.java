package dev.lokspel.practice.manager.fight.event.interfaces;

import dev.lokspel.practice.manager.fight.event.runnables.DurationRunnable;
import dev.lokspel.practice.manager.fight.event.runnables.StartRunnable;
import lombok.Getter;

@Getter
public abstract class FullRunnableInterface extends Event {

    protected final StartRunnable startRunnable;
    protected final DurationRunnable durationRunnable;

    public FullRunnableInterface(Object starter, EventData eventData) {
        super(starter, eventData);

        this.startRunnable = new StartRunnable(this);
        this.durationRunnable = new DurationRunnable(this);
    }

}
