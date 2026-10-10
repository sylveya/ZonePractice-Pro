package dev.lokspel.practice.manager.playerkit;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.ladder.abstraction.playercustom.CustomLadder;
import dev.lokspel.practice.manager.playerkit.guis.ShulkerBoxEditorGUI;
import dev.lokspel.practice.manager.playerkit.items.KitItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class PlayerKitEditing {

    private final CustomLadder customLadder;
    @Setter
    private KitItem kitItem;

    /** Non-null when the player is editing a slot INSIDE a shulker box. */
    @Setter
    private ShulkerBoxEditorGUI shulkerEditor = null;

    /** The shulker content slot index being modified (0-26), or -1 if not editing a shulker slot. */
    @Setter
    private int shulkerSlot = -1;

    public PlayerKitEditing(CustomLadder customLadder) {
        this.customLadder = customLadder;
    }

    /** True while editing a specific slot inside a shulker box. */
    public boolean isEditingShulker() {
        return shulkerEditor != null && shulkerSlot >= 0;
    }

    /** Returns to the editor, applying the selected item to the active shulker slot if needed. */
    public void returnToEditor(Player player, ItemStack selectedItem) {
        if (isEditingShulker()) {
            // Keep the editor and slot before clearing the context.
            ShulkerBoxEditorGUI editor = this.shulkerEditor;
            int slot = this.shulkerSlot;
            clearShulkerContext();
            editor.onItemSelected(slot, selectedItem != null ? selectedItem.clone() : null);
            editor.update(true);
            editor.open(player);
        } else {
            GUI mainGUI = this.customLadder.getMainGUI();
            mainGUI.update();
            mainGUI.open(player);
        }
    }

    /** Reopens the main editor after a menu closes unless another GUI has opened. */
    public void scheduleReturnToMainEditor(Player player) {
        GUI mainGUI = this.customLadder.getMainGUI();
        Bukkit.getScheduler().runTaskLater(AstralPractice.getInstance(), () -> {
            if (GUIManager.getInstance().getOpenGUI().containsKey(player)) {
                return;
            }

            mainGUI.open(player);
        }, 5L);
    }

    /** Clear shulker editing context. */
    public void clearShulkerContext() {
        this.shulkerEditor = null;
        this.shulkerSlot = -1;
    }
}
