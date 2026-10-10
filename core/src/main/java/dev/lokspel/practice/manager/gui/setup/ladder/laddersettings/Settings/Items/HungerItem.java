package dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.Items;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingItem;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingType;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingsGui;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HungerItem extends SettingItem {

    public HungerItem(SettingsGui settingsGui, NormalLadder ladder) {
        super(settingsGui, SettingType.HUNGER, ladder);
    }

    @Override
    public void updateItemStack() {
        if (ladder.isHunger())
            guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.HUNGER.ENABLED").setGlowing(true);
        else
            guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.HUNGER.DISABLED");
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        ladder.setHunger(!ladder.isHunger());

        build(true);
    }

}
