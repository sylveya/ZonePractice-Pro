package dev.lokspel.practice.command.staff.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class EnableArg {

    private EnableArg() {}

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("zpp.staff")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.NO-PERMISSION"));
            return;
        }

        if (args.length != 1) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.ENABLE.COMMAND-HELP").replace("%label%", label));
            return;
        }

        Profile profile = ProfileManager.getInstance().getProfile(player);
        if (!profile.getStatus().equals(ProfileStatus.LOBBY)) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.ENABLE.ONLY-IN-LOBBY"));
            return;
        }

        profile.setStaffMode(true);
        InventoryManager.getInstance().setLobbyInventory(player, false);
    }

}
