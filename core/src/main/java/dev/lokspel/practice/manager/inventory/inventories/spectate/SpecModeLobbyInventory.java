package dev.lokspel.practice.manager.inventory.inventories.spectate;

import dev.lokspel.practice.manager.inventory.Inventory;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Lobby.DisableSpecMode;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Lobby.RandomMatchInvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Lobby.SpecMenuInvItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class SpecModeLobbyInventory extends Inventory {

    public SpecModeLobbyInventory() {
        super(InventoryType.SPEC_MODE_LOBBY);

        this.invItems.add(new DisableSpecMode());
        this.invItems.add(new RandomMatchInvItem());
        this.invItems.add(new SpecMenuInvItem());
    }

    @Override
    protected void set(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        for (InvItem invItem : invItems) {
            int slot = invItem.getSlot();
            if (slot == -1)
                continue;

            playerInventory.setItem(slot, invItem.getItem());
        }
    }

}
