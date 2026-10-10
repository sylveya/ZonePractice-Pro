package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import org.bukkit.entity.Player;

public class SetupInvItem extends InvItem {

    public SetupInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.SETUP.ITEM"), getInt("LOBBY-BASIC.NORMAL.SETUP.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        player.performCommand("setup");
    }

}
