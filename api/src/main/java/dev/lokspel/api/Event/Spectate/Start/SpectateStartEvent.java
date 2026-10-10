package dev.lokspel.api.Event.Spectate.Start;

import dev.lokspel.api.Interface.Spectatable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class SpectateStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Spectatable spectatable;
    @Setter
    private boolean cancelled;

    public SpectateStartEvent(Player player, Spectatable spectatable) {
        this.player = player;
        this.spectatable = spectatable;
        this.cancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}