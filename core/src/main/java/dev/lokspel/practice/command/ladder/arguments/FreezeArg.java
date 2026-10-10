package dev.lokspel.practice.command.ladder.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.ladder.LadderManager;
import dev.lokspel.practice.manager.ladder.abstraction.Ladder;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public final class FreezeArg {

    private FreezeArg() {}

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("ap.ladder.freeze")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.NO-PERMISSION"));
            return;
        }

        if (args.length != 2) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.ARGUMENTS.FREEZE.COMMAND-HELP").replace("%label%", label));
            return;
        }

        NormalLadder ladder = LadderManager.getInstance().getLadder(args[1]);
        if (ladder == null) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.ARGUMENTS.FREEZE.NOT-EXISTS").replace("%ladder%", args[1]));
            return;
        }

        if (!ladder.isEnabled()) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.ARGUMENTS.FREEZE.LADDER-DISABLED").replace("%ladder%", ladder.getDisplayName()));
            return;
        }

        if (!ladder.isFrozen())
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.ARGUMENTS.FREEZE.FREEZE-SUCCESS").replace("%ladder%", ladder.getDisplayName()));
        else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.LADDER.ARGUMENTS.FREEZE.UNFREEZE-SUCCESS").replace("%ladder%", ladder.getDisplayName()));

        ladder.setFrozen(!ladder.isFrozen());
    }

    public static List<String> tabComplete(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (!player.hasPermission("ap.arena.freeze")) return arguments;

        if (args.length == 2) {
            for (Ladder ladder : LadderManager.getInstance().getLadders()) {
                if (ladder.isEnabled())
                    arguments.add(ladder.getName());
            }

            return StringUtil.copyPartialMatches(args[1], arguments, new ArrayList<>());
        }

        return arguments;
    }

}
