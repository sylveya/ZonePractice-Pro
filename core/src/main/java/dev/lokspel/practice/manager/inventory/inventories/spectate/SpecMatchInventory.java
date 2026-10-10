package dev.lokspel.practice.manager.inventory.inventories.spectate;

import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.inventory.Inventory;
import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.LeaveMatchSpecInvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match.HideSpectatorsInvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match.RandomMatchInvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match.ShowSpectatorsInvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match.SpecMenuInvItem;
import dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match.SpectatorTargetsInvItem;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import dev.lokspel.practice.util.playerutil.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class SpecMatchInventory extends Inventory {

    private static final double SPECTATOR_SPEED = ConfigManager.getDouble("SPECTATOR-SETTINGS.SPECTATOR-SPEED");

    public SpecMatchInventory() {
        super(InventoryType.SPECTATE_MATCH);

        this.invItems.add(new HideSpectatorsInvItem());
        this.invItems.add(new RandomMatchInvItem());
        this.invItems.add(new ShowSpectatorsInvItem());
        this.invItems.add(new SpecMenuInvItem());
        this.invItems.add(new SpectatorTargetsInvItem());
        this.invItems.add(new LeaveMatchSpecInvItem());
    }

    @Override
    protected void set(Player player) {
        PlayerUtil.clearPlayer(player, true, true, false);
        player.setFlySpeed((float) SPECTATOR_SPEED / 10);

        Profile profile = ProfileManager.getInstance().getProfile(player);
        profile.setStatus(ProfileStatus.SPECTATE);

        PlayerInventory playerInventory = player.getInventory();

        for (InvItem invItem : this.invItems) {
            int slot = invItem.getSlot();
            if (slot == -1)
                continue;

            if (invItem instanceof HideSpectatorsInvItem) {
                if (!InventoryManager.SPECTATOR_MODE_ENABLED)
                    continue;

                if (profile.isHideSpectators())
                    continue;
            } else if (invItem instanceof ShowSpectatorsInvItem) {
                if (!InventoryManager.SPECTATOR_MODE_ENABLED)
                    continue;

                if (!profile.isHideSpectators())
                    continue;
            } else if (invItem instanceof RandomMatchInvItem) {
                if (!InventoryManager.SPECTATOR_MODE_ENABLED)
                    continue;
            } else if (invItem instanceof SpecMenuInvItem) {
                if (!InventoryManager.SPECTATOR_MODE_ENABLED)
                    continue;

                if (!InventoryManager.SPECTATOR_MENU_ENABLED)
                    continue;
            }

            playerInventory.setItem(slot, invItem.getItem());
        }
    }

}
