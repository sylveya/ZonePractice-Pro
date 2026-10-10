package dev.lokspel.practice.util;

import dev.lokspel.practice.AstralPractice;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtil {

    private InventoryUtil() {}

    public static Inventory createInventory(String title, int row) {
        Component component = AstralPractice.getMiniMessage().deserialize(StringUtil.legacyToMiniMessage(title));
        return Bukkit.getServer().createInventory(null, row * 9, component);
    }

    /** Clones the array and each non-null item, preserving null arrays and slots. */
    public static ItemStack[] cloneItems(ItemStack[] source) {
        if (source == null) {
            return null;
        }

        ItemStack[] copy = source.clone();
        for (int i = 0; i < copy.length; i++) {
            if (copy[i] != null) {
                copy[i] = copy[i].clone();
            }
        }
        return copy;
    }

}
