package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public class SettingsInvItem extends InvItem {

    public SettingsInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.SETTINGS.ITEM"), getInt("LOBBY-BASIC.NORMAL.SETTINGS.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        if (player.hasPermission("zpp.settings.open"))
            ProfileManager.getInstance().getProfile(player).getSettingsGui().open(player);
        else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.SETTINGS.NO-PERMISSION"));
    }

}
