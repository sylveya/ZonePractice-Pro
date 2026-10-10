package dev.lokspel.api.Event.Event;

import dev.lokspel.api.Interface.Event;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EventStartEvent extends org.bukkit.event.Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Event event;
    private boolean cancelled;

    public EventStartEvent(Event event) {
        this.event = event;
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