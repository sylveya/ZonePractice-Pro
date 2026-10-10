package dev.lokspel.practice.manager.inventory.inventoryitem.spectatoritems.spectatormodeitems.Match;

import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.inventory.Inventory;
import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.inventoryitem.InvItem;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.StringUtil;
import dev.lokspel.practice.util.cooldown.CooldownObject;
import dev.lokspel.practice.util.cooldown.PlayerCooldown;
import dev.lokspel.practice.util.entityhider.PlayerHider;
import org.bukkit.entity.Player;

public class ShowSpectatorsInvItem extends InvItem {

    public ShowSpectatorsInvItem() {
        super(getItemStack("SPECTATOR.MATCH.NORMAL.SHOW-SPECTATORS.ITEM"), getInt("SPECTATOR.MATCH.NORMAL.SHOW-SPECTATORS.SLOT"));
    }

    @Override
    public void handleClickEvent(Player player) {
        if (!player.hasPermission("ap.spectate.vanish")) {
            Common.sendMMMessage(player, LanguageManager.getString("SPECTATE.NO-PERMISSIONS"));
            return;
        }

        if (!player.hasPermission("ap.bypass.cooldown") && PlayerCooldown.isActive(player, CooldownObject.SPECTATOR_VANISH)) {
            Common.sendMMMessage(player, StringUtil.replaceSecondString(LanguageManager.getString("SPECTATE.VANISH-COOLDOWN"), PlayerCooldown.getLeftInDouble(player, CooldownObject.SPECTATOR_VANISH)));
            return;
        } else
            PlayerCooldown.addCooldown(player, CooldownObject.SPECTATOR_VANISH, ConfigManager.getInt("SPECTATOR-SETTINGS.VANISH-COOLDOWN"));

        Profile profile = ProfileManager.getInstance().getProfile(player);

        profile.setHideSpectators(!profile.isHideSpectators());
        InventoryManager.getInstance().setInventory(player, Inventory.InventoryType.SPECTATE_MATCH);
        PlayerHider.getInstance().toggleSpectatorVisibility(player);
    }
}
