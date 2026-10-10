package dev.lokspel.practice.manager.gui.setup.hologram;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.leaderboard.hologram.Hologram;
import dev.lokspel.practice.manager.leaderboard.hologram.HologramManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HologramSetupManager implements Listener {

    private static HologramSetupManager instance;

    public static HologramSetupManager getInstance() {
        if (instance == null)
            instance = new HologramSetupManager();
        return instance;
    }

    private final Map<Hologram, Map<GUIType, GUI>> hologramSetupGUIs = new HashMap<>();

    public HologramSetupManager() {
        Bukkit.getPluginManager().registerEvents(this, ZonePractice.getInstance());
    }

    public void buildHologramSetupGUIs(Hologram hologram) {
        Map<GUIType, GUI> guis = new HashMap<>();

        guis.put(GUIType.Hologram_Main, GUIManager.getInstance().addGUI(new HologramMainGui(hologram)));
        guis.put(GUIType.Hologram_Ladder, GUIManager.getInstance().addGUI(new LadderGui(hologram)));

        hologramSetupGUIs.put(hologram, guis);
    }

    public void loadGUIs() {
        GUIManager.getInstance().addGUI(new HologramSummaryGui());

        for (Hologram hologram : HologramManager.getInstance().getHolograms())
            buildHologramSetupGUIs(hologram);
    }

    public void removeHologramGUIs(Hologram hologram) {
        for (GUI gui : hologramSetupGUIs.get(hologram).values()) {
            for (Player player : gui.getInGuiPlayers().keySet()) {
                GUIManager.getInstance().searchGUI(GUIType.Setup_Hub).open(player);
            }
        }

        hologramSetupGUIs.remove(hologram);
        GUIManager.getInstance().searchGUI(GUIType.Hologram_Summary).update();
    }

}
