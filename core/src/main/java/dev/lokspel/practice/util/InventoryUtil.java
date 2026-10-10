package dev.lokspel.practice.util;

import dev.lokspel.practice.ZonePractice;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public final class InventoryUtil {

    private InventoryUtil() {}

    public static Inventory createInventory(String title, int row) {
        Component component = ZonePractice.getMiniMessage().deserialize(StringUtil.legacyToMiniMessage(title));
        return Bukkit.getServer().createInventory(null, row * 9, component);
    }

}
