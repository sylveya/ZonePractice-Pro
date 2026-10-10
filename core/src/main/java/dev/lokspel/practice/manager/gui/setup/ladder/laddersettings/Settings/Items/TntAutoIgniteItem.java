package dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.Items;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingItem;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingType;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingsGui;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TntAutoIgniteItem extends SettingItem {

    public TntAutoIgniteItem(SettingsGui settingsGui, NormalLadder ladder) {
        super(settingsGui, SettingType.TNT_AUTO_IGNITE, ladder);
    }

    @Override
    public void updateItemStack() {
        if (ladder.isTntAutoIgnite())
            guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.TNT-AUTO-IGNITE.ENABLED").setGlowing(true);
        else
            guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.TNT-AUTO-IGNITE.DISABLED");
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        ladder.setTntAutoIgnite(!ladder.isTntAutoIgnite());

        build(true);
    }

}