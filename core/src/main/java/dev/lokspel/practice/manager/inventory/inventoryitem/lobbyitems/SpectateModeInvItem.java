package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.ProfileManager;
import org.bukkit.entity.Player;

public class SpectateModeInvItem extends InvItem {

    public SpectateModeInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.ENABLE-SPECTATE-MODE.ITEM"), getInt("LOBBY-BASIC.NORMAL.ENABLE-SPECTATE-MODE.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        ProfileManager.getInstance().getProfile(player).setSpectatorMode(true);
        InventoryManager.getInstance().setLobbyInventory(player, false);
    }

}
