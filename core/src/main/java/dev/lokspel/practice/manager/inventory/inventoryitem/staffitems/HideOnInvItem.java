package dev.lokspel.practice.manager.inventory.inventoryitem.staffitems;

import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.util.entityhider.PlayerHider;
import org.bukkit.entity.Player;

public class HideOnInvItem extends InvItem {

    public HideOnInvItem() {
        super(getItemStack("STAFF-MODE.NORMAL.HIDE-FROM-PLAYERS-ON.ITEM"), getInt("STAFF-MODE.NORMAL.HIDE-FROM-PLAYERS-ON.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        Profile profile = ProfileManager.getInstance().getProfile(player);
        profile.setHideFromPlayers(!profile.isHideFromPlayers());

        PlayerHider.getInstance().toggleStaffVisibility(player);
        InventoryManager.getInstance().setStaffModeInventory(player);
    }

}
