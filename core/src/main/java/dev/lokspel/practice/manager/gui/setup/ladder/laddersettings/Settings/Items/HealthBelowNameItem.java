package dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.Items;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.gui.setup.ladder.LadderSetupManager;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingItem;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingType;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingsGui;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HealthBelowNameItem extends SettingItem {

    public HealthBelowNameItem(SettingsGui settingsGui, NormalLadder ladder) {
        super(settingsGui, SettingType.HEALTH_BELOW_NAME, ladder);
    }

    @Override
    public void updateItemStack() {
        if (this.ladder.isHealthBelowName()) {
            this.guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.HEALTH-BELOW-NAME.ENABLED");
        } else {
            this.guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.HEALTH-BELOW-NAME.DISABLED");
        }
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        this.ladder.setHealthBelowName(!ladder.isHealthBelowName());

        LadderSetupManager.getInstance().getLadderSetupGUIs().get(ladder).get(GUIType.Ladder_Inventory).update();

        this.build(true);
    }
}
