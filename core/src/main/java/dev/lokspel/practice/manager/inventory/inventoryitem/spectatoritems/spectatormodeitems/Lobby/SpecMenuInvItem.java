package dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Lobby;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.spectator.SpectatorManager;
import org.bukkit.entity.Player;

public class SpecMenuInvItem extends InvItem {

    public SpecMenuInvItem() {
        super(getItemStack("SPECTATOR.LOBBY.NORMAL.MENU.ITEM"), getInt("SPECTATOR.LOBBY.NORMAL.MENU.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        SpectatorManager.getInstance().spectateMenuUse(player);
    }

}
