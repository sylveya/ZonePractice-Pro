package dev.lokspel.api.Event.Spectate.End;

import dev.lokspel.api.Interface.Match;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@Getter
public class MatchSpectateEndEvent extends SpectateEndEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;

    public MatchSpectateEndEvent(Player player, Match match) {
        super(player, match);
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