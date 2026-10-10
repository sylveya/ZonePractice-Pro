package dev.lokspel.practice.command.ffa.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class HelpArg {

    private HelpArg() {}

    public static void run(Player player, String label) {
        for (String line : LanguageManager.getList("FFA.COMMAND.HELP")) {
            Common.sendMMMessage(player, line.replace("%label%", label));
        }
    }

}
