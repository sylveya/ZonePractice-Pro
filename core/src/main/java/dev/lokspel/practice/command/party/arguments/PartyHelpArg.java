package dev.lokspel.practice.command.party.arguments;

import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

public final class PartyHelpArg {

    private PartyHelpArg() {}

    public static void HelpCommand(Player player, String label) {
        String shortcut = ConfigManager.getPartyChatShortcut();

        for (String line : LanguageManager.getList("COMMAND.PARTY.ARGUMENTS.HELP")) {
            if (line.contains("%shortcut%") && shortcut.isEmpty()) continue;

            Common.sendMMMessage(player, line
                    .replace("%label%", label)
                    .replace("%shortcut%", shortcut)
            );
        }
    }

}
