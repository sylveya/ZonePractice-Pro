package dev.lokspel.practice.manager.fight.ffa.game;

import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.arenas.FFAArena;
import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.fight.ffa.FFAManager;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIItem;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FFAArenaSelectorGui extends GUI {

    private static final String BUILD_ON = GUIFile.getString("GUIS.FFA.ARENA-SELECTOR.ICONS.ARENA.BUILD-STATUS.ENABLED");
    private static final String BUILD_OFF = GUIFile.getString("GUIS.FFA.ARENA-SELECTOR.ICONS.ARENA.BUILD-STATUS.DISABLED");
    private static final GUIItem ARENA_ITEM = GUIFile.getGuiItem("GUIS.FFA.ARENA-SELECTOR.ICONS.ARENA");
    private static final ItemStack FILLER_ITEM = GUIFile.getGuiItem("GUIS.FFA.ARENA-SELECTOR.ICONS.FILLER").get();

    private final Map<Integer, FFAArena> arenaSlots = new HashMap<>();

    public FFAArenaSelectorGui() {
        super(GUIType.FFA_Arena_Selector);

        int rows = Math.max(1, GUIFile.getInt("GUIS.FFA.ARENA-SELECTOR.ROWS"));
        this.gui.put(1, InventoryUtil.createInventory(GUIFile.getString("GUIS.FFA.ARENA-SELECTOR.TITLE"), rows));
    }

    @Override
    public void build() {
        update();
    }

    @Override
    public void update() {
        Inventory inventory = gui.get(1);
        inventory.clear();
        arenaSlots.clear();

        List<FFAArena> openArenas = ArenaManager.getInstance().getFFAArenas().stream()
                .filter(arena -> arena.isEnabled() && arena.getFfa() != null && arena.getFfa().isOpen())
                .toList();

        for (FFAArena arena : openArenas) {
            FFA ffa = arena.getFfa();

            GUIItem guiItem = ARENA_ITEM.cloneItem()
                    .replace("%arena%", arena.getDisplayName())
                    .replace("%players%", String.valueOf(ffa.getPlayers().size()))
                    .replace("%build_status%", arena.isBuild() ? BUILD_ON : BUILD_OFF)
                    .replace("%rekit_after_kill%", arena.isReKitAfterKill() ? BUILD_ON : BUILD_OFF)
                    .replace("%health_reset_on_kill%", arena.isHealthResetOnKill() ? BUILD_ON : BUILD_OFF)
                    .replace("%lobby_after_death%", arena.isLobbyAfterDeath() ? BUILD_ON : BUILD_OFF)
                    .replace("%ladders%", String.valueOf(arena.getAssignedLadders().size()));

            if (arena.getIcon() != null) {
                guiItem.setBaseItem(arena.getIcon());
            }

            int slot = inventory.firstEmpty();
            if (slot == -1) break;

            inventory.setItem(slot, guiItem.get());
            arenaSlots.put(slot, arena);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, FILLER_ITEM);
            }
        }

        this.updatePlayers();
    }

    @Override
    public void handleClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if (!arenaSlots.containsKey(slot)) return;

        FFAArena arena = arenaSlots.get(slot);
        if (arena == null) return;

        if (!FFAManager.getInstance().joinArena(player, arena)) {
            player.closeInventory();
        }
    }

}

