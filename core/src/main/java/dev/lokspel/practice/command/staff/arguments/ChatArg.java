package dev.lokspel.practice.command.staff.arguments;

import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.playerutil.PlayerUtil;
import org.bukkit.entity.Player;

public final class ChatArg {

    private ChatArg() {}

    public static void run(Player player, String[] args) {
        if (!player.hasPermission("ap.staffmode.chat")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.NO-PERMISSION"));
            return;
        }

        if (!ConfigManager.getBoolean("CHAT.STAFF-CHAT.ENABLED")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.CHAT.DISABLED"));
            return;
        }

        if (args.length == 1) {
            Profile profile = ProfileManager.getInstance().getProfile(player);
            profile.setStaffChat(!profile.isStaffChat());

            if (profile.isStaffChat())
                Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.CHAT.CHAT-ENABLED"));
            else
                Common.sendMMMessage(player, LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.CHAT.CHAT-DISABLED"));
        } else {
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                message.append(args[i]).append(" ");

            PlayerUtil.sendStaffMessage(player, message.toString());
        }
    }

    public static void run(String[] args) {
        if (!ConfigManager.getBoolean("CHAT.STAFF-CHAT.ENABLED")) {
            Common.sendConsoleMMMessage(LanguageManager.getString("COMMAND.STAFF.ARGUMENTS.CHAT.DISABLED"));
            return;
        }

        if (args.length == 1)
            Common.sendConsoleMMMessage("<red>Use: /staff chat <message>");
        else {
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                message.append(args[i]).append(" ");

            PlayerUtil.sendStaffMessage(null, message.toString());
        }
    }

}
