package dev.lokspel.api.Event;

import dev.lokspel.api.Interface.Party;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class PartyCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Party party;
    private boolean cancelled;

    public PartyCreateEvent(Party party) {
        this.party = party;
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
