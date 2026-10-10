package dev.lokspel.practice.command.ladder.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class HelpArg {

    private HelpArg() {}

    public static void run(Player player, String label) {
        if (player.hasPermission("ap.setup")) {
            for (String line : LanguageManager.getList("COMMAND.LADDER.ARGUMENTS.HELP.ADMIN"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else if (player.hasPermission("ap.ladder.freeze") || player.hasPermission("ap.ladder.stop")) {
            for (String line : LanguageManager.getList("COMMAND.LADDER.ARGUMENTS.HELP.STAFF"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.NO-PERMISSION"));
    }

    public static void run_setCommand(Player player, String label) {
        if (player.hasPermission("ap.setup")) {
            for (String line : LanguageManager.getList("COMMAND.LADDER.ARGUMENTS.HELP.SET-COMMAND"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.NO-PERMISSION"));
    }

}
