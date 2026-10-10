package dev.lokspel.api.Event.Spectate.End;

import dev.lokspel.api.Interface.Spectatable;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class SpectateEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Spectatable spectatable;

    public SpectateEndEvent(Player player, Spectatable spectatable) {
        this.player = player;
        this.spectatable = spectatable;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
