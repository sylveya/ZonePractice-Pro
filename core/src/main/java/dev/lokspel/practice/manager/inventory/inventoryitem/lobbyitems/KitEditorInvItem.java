package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import org.bukkit.entity.Player;

public class KitEditorInvItem extends InvItem {

    public KitEditorInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.KIT-EDITOR.ITEM"), getInt("LOBBY-BASIC.NORMAL.KIT-EDITOR.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        player.performCommand("editor");
    }

}
