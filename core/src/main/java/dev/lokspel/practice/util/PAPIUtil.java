package dev.lokspel.practice.util;

import dev.lokspel.practice.ZonePractice;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class PAPIUtil {

    private PAPIUtil() {}

    public static Component runThroughFormat(Player player, String line) {
        if (line == null || line.isEmpty()) {
            return Component.empty();
        }

        if (SoftDependUtil.isPAPI_ENABLED) {
            return ZonePractice.getMiniMessage().deserialize(StringUtil.legacyToMiniMessage(PlaceholderAPI.setPlaceholders(player, line)));
        }

        return ZonePractice.getMiniMessage().deserialize(StringUtil.legacyToMiniMessage(line));
    }

}
