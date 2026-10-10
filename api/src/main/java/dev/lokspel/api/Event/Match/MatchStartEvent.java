package dev.lokspel.api.Event.Match;

import dev.lokspel.api.Interface.Match;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class MatchStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;
    private boolean cancelled;

    public MatchStartEvent(Match match) {
        this.match = match;
        this.cancelled = false;
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
