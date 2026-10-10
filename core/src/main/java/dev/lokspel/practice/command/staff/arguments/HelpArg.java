package dev.lokspel.practice.command.staff.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class HelpArg {

    private HelpArg() {}

    public static void run(Player player, String label) {
        if (!player.hasPermission("zpp.staffmode")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.NO-PERMISSION"));
            return;
        }

        for (String line : LanguageManager.getList("COMMAND.STAFF.ARGUMENTS.HELP"))
            Common.sendMMMessage(player, line.replace("%label%", label));
    }

    public static void run(String label) {
        for (String line : LanguageManager.getList("COMMAND.STAFF.ARGUMENTS.HELP"))
            Common.sendConsoleMMMessage(line.replace("%label%", label));
    }

}
