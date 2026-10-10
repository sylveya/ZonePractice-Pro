package dev.lokspel.practice.manager.inventory.inventoryitem.partyitems;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.party.Party;
import dev.lokspel.practice.manager.party.PartyManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public class PartySettingsInvItem extends InvItem {

    public PartySettingsInvItem() {
        super(getItemStack("PARTY.NORMAL.PARTY-SETTINGS.ITEM"), getInt("PARTY.NORMAL.PARTY-SETTINGS.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        Party party = PartyManager.getInstance().getParty(player);
        if (party == null) return;

        if (!party.getLeader().equals(player)) {
            Common.sendMMMessage(player, LanguageManager.getString("PARTY.NOT-LEADER"));
            return;
        }

        party.getPartySettingsGui().open(player);
    }
}
