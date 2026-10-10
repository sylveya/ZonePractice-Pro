package dev.lokspel.practice.command.arena.arguments;

import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.arenas.interfaces.DisplayArena;
import dev.lokspel.practice.manager.arena.util.ArenaUtil;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public final class EnableArg {

    private EnableArg() {}

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("ap.setup")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.ARENA.NO-PERMISSION"));
            return;
        }

        if (args.length != 2) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.ENABLE.COMMAND-HELP").replace("%label%", label));
            return;
        }

        DisplayArena arena = ArenaManager.getInstance().getArena(args[1]);
        if (arena == null) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.ENABLE.NOT-EXISTS").replace("%arena%", args[1]));
            return;
        }

        if (arena.isEnabled()) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.ENABLE.ALREADY-ENABLED").replace("%arena%", arena.getName()));
            return;
        }

        if (ArenaUtil.changeStatus(player, arena)) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.ENABLE.ARENA-ENABLED").replace("%arena%", arena.getName()));
        }
    }

    public static List<String> tabComplete(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (!player.hasPermission("ap.setup")) return arguments;

        if (args.length == 2) {
            for (DisplayArena arena : ArenaManager.getInstance().getArenaList()) {
                if (!arena.isEnabled())
                    arguments.add(arena.getName());
            }

            return StringUtil.copyPartialMatches(args[1], arguments, new ArrayList<>());
        }

        return arguments;
    }

}
