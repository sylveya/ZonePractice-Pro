package dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.gui.guis.SpectatorTargetsGui;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.spectator.SpectatorManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public class SpectatorTargetsInvItem extends InvItem {

    public SpectatorTargetsInvItem() {
        super(getItemStack("SPECTATOR.MATCH.NORMAL.TARGETS.ITEM"), getInt("SPECTATOR.MATCH.NORMAL.TARGETS.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        if (!player.hasPermission("zpp.spectate.targets")) {
            Common.sendMMMessage(player, LanguageManager.getString("SPECTATE.NO-PERMISSIONS"));
            return;
        }

        if (SpectatorManager.getInstance().getSpectators().get(player) instanceof Match match) {
            new SpectatorTargetsGui(match).open(player);
        }
    }

}
