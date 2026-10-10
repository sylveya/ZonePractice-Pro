package dev.lokspel.practice.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public final class ItemSerializationUtil {

    private ItemSerializationUtil() {
    }

    @Nullable
    public static String itemStackArrayToBase64(ItemStack[] items) {
        if (items == null)
            return null;

        try {
            return Base64.getEncoder().encodeToString(ItemStack.serializeItemsAsBytes(items));
        } catch (Exception e) {
            Common.sendConsoleMMMessage("<red>Error encoding base 64 itemstack.");
        }
        return null;
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) {
        if (data == null) {
            return new ItemStack[0];
        }

        try {
            return ItemStack.deserializeItemsFromBytes(Base64.getMimeDecoder().decode(data));
        } catch (Exception e) {
            return readLegacy(data);
        }
    }

    /**
     * Migration for data saved before the switch to NBT, which is not byte compatible with it.
     * Reads it so it can be rewritten in the current format by onDisable. Delete when unused.
     */
    @SuppressWarnings("deprecation")
    private static ItemStack[] readLegacy(String data) {
        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(Base64.getMimeDecoder().decode(data)))) {
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            return items;
        } catch (Exception e) {
            Common.sendConsoleMMMessage("<red>Error decoding base 64 itemstack.");
            return null;
        }
    }
}
