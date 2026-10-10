package dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Lobby;

import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.ProfileManager;
import org.bukkit.entity.Player;

public class DisableSpecMode extends InvItem {

    public DisableSpecMode() {
        super(getItemStack("SPECTATOR.LOBBY.NORMAL.DISABLE.ITEM"), getInt("SPECTATOR.LOBBY.NORMAL.DISABLE.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        ProfileManager.getInstance().getProfile(player).setSpectatorMode(false);
        InventoryManager.getInstance().setLobbyInventory(player, false);
    }

}
