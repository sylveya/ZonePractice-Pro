package dev.lokspel.practice.command.ladder.arguments;

import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.gui.guis.ladder.LadderCreateGui;
import dev.lokspel.practice.manager.ladder.LadderManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;

import java.text.Normalizer;

public final class CreateArg {

    private CreateArg() {}

    private static final int MAX_LADDERS = ConfigManager.getInt("SETUP.MAX-LADDERS");

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("ap.setup")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.NO-PERMISSION"));
            return;
        }

        if (args.length != 2) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.ARGUMENTS.CREATE.COMMAND-HELP").replace("%label%", label));
            return;
        }

        if (MAX_LADDERS > 0 && LadderManager.getInstance().getLadders().size() >= MAX_LADDERS) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.ARGUMENTS.CREATE.REACHED-MAX")
                    .replace("%max%", String.valueOf(MAX_LADDERS)));
            return;
        }

        String name = args[1];
        String response = checkName(name);
        if (response != null) {
            Common.sendMMMessage(player, response);
            return;
        }

        new LadderCreateGui(name).open(player);
    }

    private static String checkName(String input) {
        if (input.isEmpty())
            return LanguageManager.getString("LADDER.CREATE.NO-NAME");
        else if (input.contains(" "))
            return LanguageManager.getString("LADDER.CREATE.ONLY-ALPHANUMERIC");
        else if (!Normalizer.isNormalized(input, Normalizer.Form.NFD))
            return LanguageManager.getString("LADDER.CREATE.NO-ACCENTS");
        else if (input.length() < 2)
            return LanguageManager.getString("LADDER.CREATE.FEW-CHAR");
    else if (input.length() > 20)
        return LanguageManager.getString("LADDER.CREATE.MANY-CHAR");
        else if (LadderManager.getInstance().getLadder(input) != null)
            return LanguageManager.getString("LADDER.CREATE.NAME-TAKEN");
        else
            return null;
    }

}
