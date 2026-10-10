package dev.lokspel.practice.manager.gui.setup.event;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.arena.util.ArenaWorldUtil;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.event.EventManager;
import dev.lokspel.practice.manager.fight.event.interfaces.EventData;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.gui.setup.event.eventsettings.EventMainGui;
import dev.lokspel.practice.manager.gui.setup.event.eventsettings.SettingsGui;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.util.Common;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EventSetupManager implements Listener {

    private static EventSetupManager instance;

    public static EventSetupManager getInstance() {
        if (instance == null)
            instance = new EventSetupManager();
        return instance;
    }

    @Getter
    public static final Map<ItemStack, EventData> eventMarkerList = new HashMap<>();
    @Getter
    private final Map<EventData, Map<GUIType, GUI>> eventSetupGUIs = new HashMap<>();

    public EventSetupManager() {
        Bukkit.getPluginManager().registerEvents(this, AstralPractice.getInstance());
    }

    public void buildEventSetupGUIs(EventData eventData) {
        Map<GUIType, GUI> guis = new HashMap<>();

        guis.put(GUIType.Event_Main, new EventMainGui(eventData));
        guis.put(GUIType.Event_Settings, new SettingsGui(eventData));

        eventSetupGUIs.put(eventData, guis);
    }

    public void loadGUIs() {
        GUIManager.getInstance().addGUI(new EventSummaryGui());

        for (EventData eventData : EventManager.getInstance().getEventData().values())
            buildEventSetupGUIs(eventData);
    }

    @EventHandler
    public void onEventCornerMarkerUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Profile profile = ProfileManager.getInstance().getProfile(player);

        Action action = e.getAction();
        ItemStack item = e.getItem();
        Block block = e.getClickedBlock();

        switch (profile.getStatus()) {
            case MATCH:
            case FFA:
            case EVENT:
                return;
        }

        if (!player.hasPermission("zpp.setup")) return;
        if (!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (item == null) return;
        if (block == null) return;

        if (!eventMarkerList.containsKey(item)) return;
        EventData eventData = eventMarkerList.get(item);
        if (eventData == null) return;

        e.setCancelled(true);
        if (eventData.isEnabled()) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.SETUP.EVENT.CANT-EDIT-ENABLED"));
            return;
        }

        if (!player.getWorld().equals(ArenaWorldUtil.getArenasWorld())) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.SETUP.EVENT.ARENAS-WORLD"));
            return;
        }

        try {
            if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                eventData.setCuboidLoc1(block.getLocation());
                Common.sendMMMessage(player, LanguageManager.getString("COMMAND.SETUP.EVENT.SET-FIRST-CORNER").replace("%event%", eventData.getType().getName()));
            } else {
                eventData.setCuboidLoc2(block.getLocation());
                Common.sendMMMessage(player, LanguageManager.getString("COMMAND.SETUP.EVENT.SET-SECOND-CORNER").replace("%event%", eventData.getType().getName()));
            }
        } catch (Exception exception) {
            Common.sendMMMessage(player, "<red>" + exception.getMessage());
            return;
        }

        eventSetupGUIs.get(eventData).get(GUIType.Event_Main).update();
    }

}
