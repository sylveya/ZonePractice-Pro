package dev.lokspel.practice.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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
            Common.sendConsoleMMMessage("<red>Error decoding base 64 itemstack.");
            return null;
        }
    }
}
