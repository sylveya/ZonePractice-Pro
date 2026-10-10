package dev.lokspel.api.Event.Spectate.Start;

import dev.lokspel.api.Interface.Event;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@Getter
public class EventSpectateStartEvent extends SpectateStartEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Event event;

    public EventSpectateStartEvent(Player player, Event event) {
        super(player, event);
        this.event = event;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
