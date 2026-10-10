package dev.lokspel.practice.manager.inventory.inventories;

import dev.lokspel.practice.manager.inventory.Inventory;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.queueitems.MatchQueueLeaveInvItem;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class MatchQueueInventory extends Inventory {

    public MatchQueueInventory() {
        super(InventoryType.MATCH_QUEUE);

        this.invItems.add(new MatchQueueLeaveInvItem());
    }

    @Override
    protected void set(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        Profile profile = ProfileManager.getInstance().getProfile(player);

        for (InvItem invItem : invItems) {
            int slot = invItem.getSlot();
            if (slot == -1)
                continue;

            if (invItem instanceof MatchQueueLeaveInvItem) {
                if (!profile.getStatus().equals(ProfileStatus.QUEUE))
                    continue;

                playerInventory.setItem(slot, invItem.getItem());
            }
        }
    }

}
