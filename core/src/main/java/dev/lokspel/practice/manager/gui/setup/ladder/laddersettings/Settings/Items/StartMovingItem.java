package dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.Items;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingItem;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingType;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingsGui;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StartMovingItem extends SettingItem {

    public StartMovingItem(SettingsGui settingsGui, final NormalLadder ladder) {
        super(settingsGui, SettingType.START_MOVING, ladder);
    }

    @Override
    public void updateItemStack() {
        if (ladder.isStartMove())
            this.guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.START-MOVING.ENABLED").setGlowing(true);
        else
            this.guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.START-MOVING.DISABLED");
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        ladder.setStartMove(!ladder.isStartMove());

        this.build(true);
    }
}
