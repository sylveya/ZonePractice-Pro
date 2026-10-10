package dev.lokspel.practice.manager.inventory.inventoryitem.lobbyitems;

import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.party.PartyManager;
import org.bukkit.entity.Player;

public class PartyCreateInvItem extends InvItem {

    public PartyCreateInvItem() {
        super(getItemStack("LOBBY-BASIC.NORMAL.PARTY-CREATE.ITEM"), getInt("LOBBY-BASIC.NORMAL.PARTY-CREATE.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        PartyManager.getInstance().createParty(player);
    }

}
