package dev.lokspel.api.Event.Match;

import dev.lokspel.api.Interface.Match;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class MatchRoundEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;
    private final int round;

    public MatchRoundEndEvent(Match match, int round) {
        this.match = match;
        this.round = round;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
