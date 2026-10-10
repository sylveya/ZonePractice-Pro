package dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.Items;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingItem;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingType;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingsGui;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HeartsItem extends SettingItem {

    public HeartsItem(SettingsGui settingsGui, NormalLadder ladder) {
        super(settingsGui, SettingType.HEARTS, ladder);
    }

    @Override
    public void updateItemStack() {
        guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.HEARTS")
                .replace("%hearts%", String.valueOf(ladder.getHearts()));
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        int hearts = ladder.getHearts();

        if (e.getClick().isLeftClick() && hearts > 1)
            ladder.setHearts(hearts - 1);
        else if (e.getClick().isRightClick() && hearts < 20)
            ladder.setHearts(hearts + 1);

        settingsGui.build();
    }
}
