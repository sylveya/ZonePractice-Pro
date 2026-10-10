package dev.lokspel.practice.manager.inventory.inventoryitem.staffitems;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.spectator.SpectatorManager;
import dev.lokspel.practice.util.interfaces.Spectatable;
import org.bukkit.entity.Player;

public class LeaveSpecInvItem extends InvItem {

    public LeaveSpecInvItem() {
        super(getItemStack("STAFF-MODE.NORMAL.LEAVE-SPECTATE.ITEM"), getInt("STAFF-MODE.NORMAL.LEAVE-SPECTATE.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        Spectatable spectatable = SpectatorManager.getInstance().getSpectators().get(player);
        if (spectatable != null)
            spectatable.removeSpectator(player);
    }
}
