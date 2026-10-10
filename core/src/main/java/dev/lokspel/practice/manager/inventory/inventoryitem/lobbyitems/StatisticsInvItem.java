package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.gui.guis.leaderboard.LbSelectorGui;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.ProfileManager;
import org.bukkit.entity.Player;

public class StatisticsInvItem extends InvItem {

    public StatisticsInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.STATISTICS.ITEM"), getInt("LOBBY-BASIC.NORMAL.STATISTICS.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        new LbSelectorGui(player, ProfileManager.getInstance().getProfile(player))
                .open(player);
    }
}
