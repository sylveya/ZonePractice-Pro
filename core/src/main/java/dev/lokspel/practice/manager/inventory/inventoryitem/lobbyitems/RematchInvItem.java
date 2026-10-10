package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.fight.match.MatchManager;
import dev.lokspel.practice.manager.fight.match.util.RematchRequest;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import org.bukkit.entity.Player;

public class RematchInvItem extends InvItem {

    public RematchInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.REMATCH.ITEM"), getInt("LOBBY-BASIC.NORMAL.REMATCH.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        RematchRequest rematchRequest = MatchManager.getInstance().getRematchRequest(player);

        if (rematchRequest != null)
            rematchRequest.sendRematchRequest(player);
    }

}
