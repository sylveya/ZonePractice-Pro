package dev.lokspel.api.Event.Spectate.Start;

import dev.lokspel.api.Interface.FFA;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@Getter
public class FFASpectateStartEvent extends SpectateStartEvent {

    private static final HandlerList handlers = new HandlerList();

    private final FFA ffa;

    public FFASpectateStartEvent(Player player, FFA ffa) {
        super(player, ffa);
        this.ffa = ffa;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
