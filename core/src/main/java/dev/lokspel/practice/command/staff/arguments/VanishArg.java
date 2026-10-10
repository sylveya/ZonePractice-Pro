package dev.lokspel.practice.command.staff.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.entityhider.PlayerHider;
import org.bukkit.entity.Player;

public final class VanishArg {

    private VanishArg() {}

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("ap.staffmode")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.NO-PERMISSION"));
            return;
        }

        if (args.length != 1) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.VANISH.COMMAND-HELP").replace("%label%", label));
            return;
        }

        Profile profile = ProfileManager.getInstance().getProfile(player);
        if (profile.getStatus().equals(ProfileStatus.MATCH) || profile.getStatus().equals(ProfileStatus.EVENT)) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.VANISH.CANT-USE"));
            return;
        }

        profile.setHideFromPlayers(!profile.isHideFromPlayers());
        PlayerHider.getInstance().toggleStaffVisibility(player);

        if (profile.isStaffMode()) {
            InventoryManager.getInstance().setStaffModeInventory(player);
        }

        if (profile.isHideFromPlayers())
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.VANISH.INVISIBLE"));
        else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.VANISH.VISIBLE"));
    }

}
