package dev.lokspel.practice.command.practice.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class HelpArg {

    private HelpArg() {}

    public static void run(Player player, String label) {
        if (player.hasPermission("ap.admin")) {
            for (String line : LanguageManager.getList("COMMAND.PRACTICE.ARGUMENTS.HELP.ADMIN"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else if (player.hasPermission("ap.staff")) {
            for (String line : LanguageManager.getList("COMMAND.PRACTICE.ARGUMENTS.HELP.STAFF"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.NO-PERMISSION"));
    }

    public static void run(String label) {
        for (String line : LanguageManager.getList("COMMAND.PRACTICE.ARGUMENTS.HELP.ADMIN"))
            Common.sendConsoleMMMessage(line.replace("%label%", label));
    }

}
