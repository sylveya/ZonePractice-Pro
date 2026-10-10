package dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.spectator.SpectatorManager;
import org.bukkit.entity.Player;

public class RandomMatchInvItem extends InvItem {

    public RandomMatchInvItem() {
        super(getItemStack("SPECTATOR.MATCH.NORMAL.RANDOM.ITEM"), getInt("SPECTATOR.MATCH.NORMAL.RANDOM.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        SpectatorManager.spectateRandomMatchItemUse(player);
    }

}
