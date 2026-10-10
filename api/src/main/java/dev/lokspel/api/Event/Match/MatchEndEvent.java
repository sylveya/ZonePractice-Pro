package dev.lokspel.api.Event.Match;

import dev.lokspel.api.Interface.Match;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class MatchEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;

    public MatchEndEvent(Match match) {
        this.match = match;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
