package dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.Items;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingItem;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingType;
import dev.lokspel.practice.manager.gui.setup.ladder.laddersettings.Settings.SettingsGui;
import dev.lokspel.practice.manager.ladder.type.Creeper;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CreeperExplosionDelayItem extends SettingItem {

    private final Creeper creeper;

    public CreeperExplosionDelayItem(SettingsGui settingsGui, Creeper creeper) {
        super(settingsGui, SettingType.CREEPER_EXPLOSION_DELAY, creeper);
        this.creeper = creeper;
    }

    @Override
    public void updateItemStack() {
        this.guiItem = GUIFile.getGuiItem("GUIS.SETUP.LADDER.SETTINGS.ICONS.CREEPER-EXPLOSION-DELAY")
                .replace("%creeperExplosionDelay%", String.valueOf(creeper.getCreeperExplosionDelay()));
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        double creeperExplosionDelay = creeper.getCreeperExplosionDelay();

        if (e.getClick().isLeftClick() && creeperExplosionDelay > 0.5)
            creeper.setCreeperExplosionDelay(creeperExplosionDelay - 0.5);
        else if (e.getClick().isRightClick() && creeperExplosionDelay < 10.0)
            creeper.setCreeperExplosionDelay(creeperExplosionDelay + 0.5);

        build(true);
    }

}