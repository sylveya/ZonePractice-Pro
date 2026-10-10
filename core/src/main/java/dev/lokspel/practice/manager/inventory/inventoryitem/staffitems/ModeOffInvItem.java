package dev.lokspel.practice.manager.inventory.inventoryitem.staffitems;

import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.spectator.SpectatorManager;
import org.bukkit.entity.Player;

public class ModeOffInvItem extends InvItem {

    public ModeOffInvItem() {
        super(getItemStack("STAFF-MODE.NORMAL.TURN-OFF.ITEM"), getInt("STAFF-MODE.NORMAL.TURN-OFF.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        ProfileManager.getInstance().getProfile(player).setStaffMode(false);

        if (SpectatorManager.getInstance().getSpectators().containsKey(player)) {
            SpectatorManager.getInstance().getSpectators().get(player).removeSpectator(player);
        } else {
            InventoryManager.getInstance().setLobbyInventory(player, false);
        }
    }
}
