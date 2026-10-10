package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.ProfileManager;
import org.bukkit.entity.Player;

public class StaffMode extends InvItem {

    public StaffMode() {
        super(getItemStack("LOBBY-BASIC.NORMAL.STAFF-MODE.ITEM"), getInt("LOBBY-BASIC.NORMAL.STAFF-MODE.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        ProfileManager.getInstance().getProfile(player).setStaffMode(true);
        InventoryManager.getInstance().setLobbyInventory(player, false);
    }

}
