package dev.lokspel.api.Event.Queue;

import dev.lokspel.api.Interface.Queue;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class QueueStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Queue queue;
    private boolean cancelled;

    public QueueStartEvent(Queue queue) {
        this.queue = queue;
        cancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

}
