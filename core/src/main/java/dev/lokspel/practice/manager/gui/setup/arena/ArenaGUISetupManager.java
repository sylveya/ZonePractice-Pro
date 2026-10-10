package dev.lokspel.practice.manager.gui.setup.arena;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.arenas.Arena;
import dev.lokspel.practice.manager.arena.arenas.FFAArena;
import dev.lokspel.practice.manager.arena.arenas.interfaces.DisplayArena;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.gui.setup.arena.arenasettings.normal.ArenaMainGui;
import dev.lokspel.practice.manager.gui.setup.arena.arenasettings.normal.CopyGui;
import dev.lokspel.practice.manager.gui.setup.arena.arenasettings.normal.LadderSingleGui;
import dev.lokspel.practice.manager.gui.setup.arena.arenasettings.normal.LadderTypeGui;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ArenaGUISetupManager implements Listener {

    private static ArenaGUISetupManager instance;

    public static ArenaGUISetupManager getInstance() {
        if (instance == null)
            instance = new ArenaGUISetupManager();
        return instance;
    }

    private final Map<DisplayArena, Map<GUIType, GUI>> arenaSetupGUIs = new HashMap<>();

    public ArenaGUISetupManager() {
        Bukkit.getPluginManager().registerEvents(this, ZonePractice.getInstance());
    }

    public void buildArenaSetupGUIs(DisplayArena arena) {
        Map<GUIType, GUI> guis = new HashMap<>();

        if (arena instanceof FFAArena) {
            guis.put(GUIType.Arena_Main, new dev.lokspel.practice.manager.gui.setup.arena.arenasettings.ffa.ArenaMainGui((FFAArena) arena));
            guis.put(GUIType.Arena_Ladders_Single, new dev.lokspel.practice.manager.gui.setup.arena.arenasettings.ffa.LadderSingleGui((FFAArena) arena));
        } else {
            guis.put(GUIType.Arena_Main, new ArenaMainGui((Arena) arena));
            guis.put(GUIType.Arena_Ladders_Single, new LadderSingleGui((Arena) arena));
            guis.put(GUIType.Arena_Ladders_Type, new LadderTypeGui((Arena) arena));
            if (arena.isBuild())
                guis.put(GUIType.Arena_Copy, new CopyGui((Arena) arena));
        }

        arenaSetupGUIs.put(arena, guis);
    }

    public void loadGUIs() {
        Bukkit.getScheduler().runTaskAsynchronously(ZonePractice.getInstance(), () ->
        {
            GUIManager.getInstance().addGUI(new ArenaSummaryGui());

            for (DisplayArena arena : new ArrayList<>(ArenaManager.getInstance().getArenaList())) {
                buildArenaSetupGUIs(arena);
            }
        });
    }

    public void removeArenaGUIs(DisplayArena arena) {
        for (GUI gui : arenaSetupGUIs.get(arena).values()) {
            for (Player player : gui.getInGuiPlayers().keySet()) {
                GUIManager.getInstance().searchGUI(GUIType.Arena_Summary).open(player);
            }
        }

        arenaSetupGUIs.remove(arena);
        GUIManager.getInstance().searchGUI(GUIType.Arena_Summary).update();
    }

    @EventHandler
    public void onArenaCornerMarkerUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Profile profile = ProfileManager.getInstance().getProfile(player);

        Action action = e.getAction();
        ItemStack item = e.getItem();

        switch (profile.getStatus()) {
            case MATCH:
            case FFA:
            case EVENT:
                return;
        }

        if (!player.hasPermission("zpp.setup")) return;
        if (!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (item == null) return;

        if (!ArenaSetupUtil.getArenaMarkerList().containsKey(item)) return;
        DisplayArena arena = ArenaSetupUtil.getArenaMarkerList().get(item);
        if (arena == null) return;

        e.setCancelled(true);

        if (action.equals(Action.LEFT_CLICK_BLOCK))
            player.performCommand("arena set corner " + arena.getName() + " 1");
        else
            player.performCommand("arena set corner " + arena.getName() + " 2");
    }

}
