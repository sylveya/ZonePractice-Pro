package dev.lokspel.practice.command.party.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.party.PartyManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class PartyPublicArg {

    private PartyPublicArg() {}

    public static void PublicCommand(Player player) {
        if (!player.hasPermission("zpp.party.view")) {
            Common.sendMMMessage(player, LanguageManager.getString("PARTY.NO-PERMISSION"));
            return;
        }

        if (!PartyManager.getInstance().hasJoinablePublicParty()) {
            Common.sendMMMessage(player, LanguageManager.getString("PARTY.NO-JOINABLE-PARTY"));
            return;
        }

        GUIManager.getInstance().searchGUI(GUIType.Party_PublicParties).open(player);
    }

}
