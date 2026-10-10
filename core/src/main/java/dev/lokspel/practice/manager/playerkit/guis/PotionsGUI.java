package dev.lokspel.practice.manager.playerkit.guis;

import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.playerkit.PlayerKitEditing;
import dev.lokspel.practice.manager.playerkit.PlayerKitManager;
import dev.lokspel.practice.manager.playerkit.StaticItems;
import dev.lokspel.practice.manager.playerkit.items.KitItem;
import dev.lokspel.practice.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PotionsGUI extends GUI {

    private static final int SPACES = 9 * 5;

    public PotionsGUI() {
        super(GUIType.PlayerCustom_Potions);

        this.build();
    }

    @Override
    public void build() {
        Inventory drinkableInv = InventoryUtil.createInventory(StaticItems.POTIONS_GUI_TITLE, 6);
        this.gui.put(1, drinkableInv);
        setIcons(drinkableInv, StaticItems.POTIONS_GUI_SWITCH_SPLASH_ICON, StaticItems.POTIONS_GUI_DRINKABLE_POTIONS);

        Inventory splashInv = InventoryUtil.createInventory(StaticItems.POTIONS_GUI_TITLE, 6);
        this.gui.put(2, splashInv);
        setIcons(splashInv, StaticItems.POTIONS_GUI_SWITCH_DRINKABLE_ICON, StaticItems.POTIONS_GUI_SPLASH_POTIONS);
    }

    private static void setIcons(final Inventory inventory, final ItemStack switchItem, final List<ItemStack> potionItems) {
        inventory.setItem(45, StaticItems.POTIONS_GUI_BACK_ICON);
        inventory.setItem(49, switchItem);

        for (ItemStack itemStack : potionItems) {
            if (inventory.firstEmpty() >= SPACES) {
                break;
            }

            inventory.addItem(itemStack);
        }
    }

    @Override
    public void update() {
        updatePlayers();
    }

    @Override
    public void handleClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        int page = inGuiPlayers.get(player);

        if (e.getClickedInventory() == null) return;
        if (e.getInventory().getSize() <= slot) return;

        if (slot == 45) {
            GUIManager.getInstance().searchGUI(GUIType.PlayerCustom_Category).open(player);
        } else if (slot == 49) {
            open(player, page == 1 ? 2 : 1);
        } else {
            ItemStack item = gui.get(page).getItem(slot);
            if (item == null || item.getType() == Material.AIR) {
                return;
            }

            PlayerKitEditing playerKitEditing = PlayerKitManager.getInstance().getEditing().get(player);
            KitItem editing = playerKitEditing.getKitItem();
            editing.reset();
            editing.setItemStack(item.clone());

            playerKitEditing.returnToEditor(player, item);
        }
    }

    @Override
    public void handleCloseEvent(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        PlayerKitEditing playerKitEditing = PlayerKitManager.getInstance().getEditing().get(player);
        playerKitEditing.scheduleReturnToMainEditor(player);
    }

}
