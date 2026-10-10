package dev.lokspel.practice.command.event.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class HelpArg {

    private HelpArg() {}

    public static void run(Player player, String label) {
        if (player.hasPermission("ap.setup") && player.hasPermission("ap.event.stop")) {
            for (String line : LanguageManager.getList("COMMAND.EVENT.ARGUMENTS.HELP.STAFF"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else if (player.hasPermission("ap.event.join") || player.hasPermission("ap.event.host")) {
            for (String line : LanguageManager.getList("COMMAND.EVENT.ARGUMENTS.HELP.USER"))
                Common.sendMMMessage(player, line.replace("%label%", label));
        } else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.EVENT.NO-PERMISSION"));
    }

}
