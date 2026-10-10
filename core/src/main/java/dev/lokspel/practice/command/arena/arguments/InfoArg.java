package dev.lokspel.practice.command.arena.arguments;

import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.arenas.interfaces.DisplayArena;
import dev.lokspel.practice.manager.arena.util.ArenaUtil;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.StringUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class InfoArg {

    private InfoArg() {}

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("ap.setup")) {
            Common.sendMMMessage(player, LanguageManager.getString("command.arena.no-permission"));
            return;
        }

        if (args.length != 2) {
            Common.sendMMMessage(player, LanguageManager.getString("command.arena.arguments.info.command-help").replace("%label%", label));
            return;
        }

        DisplayArena arena = ArenaManager.getInstance().getArena(args[1]);
        if (arena == null) {
            Common.sendMMMessage(player, LanguageManager.getString("command.arena.arguments.info.not-exists").replace("%arena%", args[1]));
            return;
        }

        List<String> ladderNames = ArenaUtil.getLadderNames(arena);
        for (String line : LanguageManager.getList("command.arena.arguments.info.arena-info")) {
            Common.sendMMMessage(player, line
                    .replace("%arena%", arena.getName())
                    .replace("%type%", arena.getType().getName())
                    .replace("%icon%", arena.getIcon() != null ? LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.INFO.STATUS-NAMES.SET") : LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.INFO.STATUS-NAMES.NOT-SET"))
                    .replace("%displayName%", arena.getDisplayName())
                    .replace("%ladders%", (ladderNames.isEmpty() ? StringUtil.CC("<red>NULL") : ladderNames.toString().replace("]", "").replace("[", "")))
                    .replace("%corner1%", ArenaUtil.convertLocation(arena.getCorner1()))
                    .replace("%corner2%", ArenaUtil.convertLocation(arena.getCorner2()))
                    .replace("%position1%", ArenaUtil.convertLocation(arena.getPosition1()))
                    .replace("%position2%", ArenaUtil.convertLocation(arena.getPosition2()))
                    .replace("%partyFfaCenter%", ArenaUtil.convertLocation(arena.getPartyFfaCenter()))
                    .replace("%status%", arena.isEnabled() ? LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.INFO.STATUS-NAMES.ENABLED") : LanguageManager.getString("COMMAND.ARENA.ARGUMENTS.INFO.STATUS-NAMES.DISABLED"))
            );
        }
    }

    public static List<String> tabComplete(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (!player.hasPermission("ap.setup")) return arguments;

        if (args.length == 2) {
            for (DisplayArena arena : ArenaManager.getInstance().getArenaList())
                arguments.add(arena.getName());

            return org.bukkit.util.StringUtil.copyPartialMatches(args[1], arguments, new ArrayList<>());
        }

        return arguments;
    }

}
