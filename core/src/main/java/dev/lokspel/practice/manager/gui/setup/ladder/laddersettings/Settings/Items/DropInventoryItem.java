package dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.Items;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingItem;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingType;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingsGui;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DropInventoryItem extends SettingItem {

    public DropInventoryItem(SettingsGui settingsGui, NormalLadder ladder) {
        super(settingsGui, SettingType.DROP_INVENTORY, ladder);
    }

    @Override
    public void updateItemStack() {
        if (ladder.isDropInventory())
            guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.DROP-INVENTORY-PARTY-GAMES.ENABLED").setGlowing(true);
        else
            guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.DROP-INVENTORY-PARTY-GAMES.DISABLED");
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        ladder.setDropInventory(!ladder.isDropInventory());

        build(true);
    }

}
