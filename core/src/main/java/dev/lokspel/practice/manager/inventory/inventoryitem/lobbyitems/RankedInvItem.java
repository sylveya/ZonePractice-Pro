package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import org.bukkit.entity.Player;

public class RankedInvItem extends InvItem {

    public RankedInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.RANKED.ITEM"), getInt("LOBBY-BASIC.NORMAL.RANKED.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        player.performCommand("ranked");
    }

}
