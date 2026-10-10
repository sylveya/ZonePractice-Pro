package dev.lokspel.practice.command.practice.arguments;

import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.gui.guis.profile.ProfileSetupGui;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.server.ServerManager;
import dev.lokspel.practice.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public final class InfoArg {

    private InfoArg() {}

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("ap.practice.info")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.NO-PERMISSION"));
            return;
        }

        if (args.length != 2) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.ARGUMENTS.INFO.COMMAND-HELP").replace("%label%", label));
            return;
        }

        Profile target = ProfileManager.getInstance().getProfile(ServerManager.getInstance().resolvePlayer(args[1]));
        if (target == null) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.ARGUMENTS.INFO.TARGET-NOT-FOUND").replace("%target%", args[1]));
            return;
        }

        if (!player.equals(target.getPlayer()) && target.getPlayer().isOp() && ConfigManager.getBoolean("ADMIN-SETTINGS.OP-BYPASS-INFOGUI")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.ARGUMENTS.INFO.TARGET-OP").replace("%target%", target.getPlayer().getName()));
            return;
        }

        new ProfileSetupGui(target).open(player);
    }

    public static List<String> tabComplete(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();

        if (!player.hasPermission("ap.practice.info")) return arguments;

        if (args.length == 2) {
            for (Player online : Bukkit.getOnlinePlayers())
                arguments.add(online.getName());

            return StringUtil.copyPartialMatches(args[1], arguments, new ArrayList<>());
        }

        return arguments;
    }

}
