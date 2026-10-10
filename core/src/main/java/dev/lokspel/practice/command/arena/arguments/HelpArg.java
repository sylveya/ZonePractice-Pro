package dev.lokspel.practice.command.arena.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class HelpArg {

    private HelpArg() {}

    public static void run(Player player, String label) {
        if (player.hasPermission("zpp.setup")) {
            for (String line : LanguageManager.getList("COMMAND.ARENA.ARGUMENTS.HELP.ADMIN"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else if (player.hasPermission("zpp.arena.freeze") || player.hasPermission("zpp.arena.stop")) {
            for (String line : LanguageManager.getList("COMMAND.ARENA.ARGUMENTS.HELP.STAFF"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else
            Common.sendMMMessage(player, LanguageManager.getString("command.arena.no-permission"));
    }

    public static void run_setCommand(Player player, String label) {
        if (player.hasPermission("zpp.setup")) {
            for (String line : LanguageManager.getList("COMMAND.ARENA.ARGUMENTS.HELP.SET-COMMAND"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else
            Common.sendMMMessage(player, LanguageManager.getString("command.arena.no-permission"));
    }

}
