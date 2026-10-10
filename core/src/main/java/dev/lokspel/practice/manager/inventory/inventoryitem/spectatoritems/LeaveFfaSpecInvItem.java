package dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems;

import dev.lokspel.practice.manager.fight.ffa.FFAManager;
import dev.lokspel.practice.manager.fight.ffa.game.FFA;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import org.bukkit.entity.Player;

public class LeaveFfaSpecInvItem extends InvItem {

    public LeaveFfaSpecInvItem() {
        super(getItemStack("SPECTATOR.FFA.NORMAL.LEAVE.ITEM"), getInt("SPECTATOR.FFA.NORMAL.LEAVE.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        FFA ffa = FFAManager.getInstance().getFFABySpectator(player);
        if (ffa != null)
            ffa.removeSpectator(player);
    }

}
