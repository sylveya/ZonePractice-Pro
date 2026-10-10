package dev.lokspel.practice.util;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class ChatFormatUtil {

    private ChatFormatUtil() {}

    private static String normalizeStaticSpacing(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return text.replaceAll(" {2,}", " ");
    }

    public static String buildPartyChatMessage(Player player) {
        Profile profile = ProfileManager.getInstance().getProfile(player);

        String playerName = profile != null
                ? AstralPractice.getMiniMessage().serialize(
                NameFormatUtil.resolveFullName(profile, player.getName())
        )
                : player.getName();

        return normalizeStaticSpacing(
                LanguageManager.getString("GENERAL-CHAT.PARTY-CHAT")
                        .replace("%%player%%", playerName)
        );
    }

    public static String buildStaffChatMessage(Player player) {
        Profile profile = ProfileManager.getInstance().getProfile(player);

        String playerName = profile != null
                ? AstralPractice.getMiniMessage().serialize(
                NameFormatUtil.resolveFullName(profile, player.getName())
        )
                : player.getName();

        return normalizeStaticSpacing(
                LanguageManager.getString("GENERAL-CHAT.STAFF-CHAT")
                        .replace("%%player%%", playerName)
        );
    }

    public static String buildServerChatMessage(Profile profile, Player player) {
        String format;

        if (ConfigManager.getBoolean("PLAYER.GROUP-CHAT.ENABLED")) {
            Group group = profile.getGroup();

            if (group != null && group.getChatFormat() != null) {
                format = group.getChatFormat();
            } else {
                format = LanguageManager.getString("GENERAL-CHAT.SERVER-CHAT");
            }
        } else {
            format = LanguageManager.getString("GENERAL-CHAT.SERVER-CHAT");
        }

        String decoratedPlayer = AstralPractice.getMiniMessage()
                .serialize(NameFormatUtil.resolveFullName(profile, player.getName()));

        return normalizeStaticSpacing(
                format.replace("%%player%%", decoratedPlayer)
        );
    }

    public static List<Player> getStaffRecipients() {
        List<Player> staff = new ArrayList<>();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("zpp.staffmode.chat")) {
                staff.add(online);
            }
        }

        return staff;
    }
}