package dev.lokspel.api.Event;

import dev.lokspel.api.Interface.FFA;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class FFARemovePlayerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final FFA ffa;
    private final Player player;

    public FFARemovePlayerEvent(FFA ffa, Player player) {
        this.ffa = ffa;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
