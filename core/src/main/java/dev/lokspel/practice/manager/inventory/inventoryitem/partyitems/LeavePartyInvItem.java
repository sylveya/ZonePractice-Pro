package dev.lokspel.practice.manager.inventory.inventoryitem.partyitems;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.party.Party;
import dev.lokspel.practice.manager.party.PartyManager;
import org.bukkit.entity.Player;

public class LeavePartyInvItem extends InvItem {

    public LeavePartyInvItem() {
        super(getItemStack("PARTY.NORMAL.LEAVE-PARTY.ITEM"), getInt("PARTY.NORMAL.LEAVE-PARTY.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        Party party = PartyManager.getInstance().getParty(player);
        if (party == null) return;

        party.removeMember(player, false);
    }

}
