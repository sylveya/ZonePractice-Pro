package dev.lokspel.practice.manager.arena.arenas.interfaces;

import dev.lokspel.practice.manager.arena.ArenaFile;
import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.ArenaType;
import dev.lokspel.practice.manager.arena.arenas.Arena;
import dev.lokspel.practice.manager.arena.arenas.ArenaCopy;
import dev.lokspel.practice.manager.arena.arenas.FFAArena;
import dev.lokspel.practice.manager.arena.setup.SpawnMarkerManager;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.ffa.game.FFA;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIItem;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.gui.setup.arena.ArenaGUISetupManager;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.util.Common;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class DisplayArena extends NormalArena {

    private static final boolean DEFAULT_ICON = ConfigManager.getBoolean("ARENA.DEFAULT-ICON.ENABLED");
    private static final GUIItem DEFAULT_ICON_ITEM = ConfigManager.getGuiItem("ARENA.DEFAULT-ICON.ICON");

    protected final ArenaFile arenaFile;
    protected ItemStack icon;
    @Setter
    protected String displayName;

    protected boolean enabled;
    protected final ArenaType type;
    @Setter
    protected boolean build;
    @Setter
    protected Set<NormalLadder> assignedLadders = new HashSet<>();

    protected DisplayArena(String name, ArenaType type) {
        super(name);

        this.type = type;
        this.displayName = name;

        arenaFile = new ArenaFile(this);
    }

    public void setIcon(final ItemStack icon) {
        if (icon == null || icon.getType().equals(Material.AIR)) {
            return;
        }

        this.icon = icon.clone();

        if (icon.hasItemMeta()) {
            String iconDisplayName = Common.getItemDisplayNameMiniMessage(icon);
            this.displayName = iconDisplayName.isBlank() ? name : iconDisplayName;
        } else {
            this.displayName = name;
        }
    }

    public ItemStack getIcon() {
        if (this.icon == null) {
            if (DEFAULT_ICON) {
                GUIItem guiItem = DEFAULT_ICON_ITEM.cloneItem();
                guiItem.replace("%arena%", this.name);
                return guiItem.get().clone();
            }
            return null;
        }
        return this.icon.clone();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!enabled) {
            if (this instanceof FFAArena) {
                FFA ffa = ((FFAArena) this).getFfa();
                if (ffa != null) {
                    ffa.close(LanguageManager.getString("FFA.ALL-LADDERS-DISABLED"));
                }
            }
        }

        if (enabled) {
            // Clear spawn markers when arena is enabled
            SpawnMarkerManager.getInstance().clearMarkers(this);

            if (ArenaManager.LOAD_CHUNKS) {
                this.loadChunks();

                if (this instanceof Arena arena) {
                    if (arena.getCopies() != null && !arena.getCopies().isEmpty()) {
                        for (ArenaCopy copy : arena.getCopies()) {
                            copy.loadChunks();
                        }
                    }
                }
            }
        }

        if (ArenaGUISetupManager.getInstance().getArenaSetupGUIs().containsKey(this)) {
            ArenaGUISetupManager.getInstance().getArenaSetupGUIs().get(this).get(GUIType.Arena_Main).update();
        }

        GUI arenaSummary = GUIManager.getInstance().searchGUI(GUIType.Arena_Summary);
        if (arenaSummary != null) {
            arenaSummary.update();
        }
    }

    public abstract void setData();

    public abstract void getData();

    public abstract boolean isReadyToEnable();

    public abstract Set<NormalLadder> getAssignableLadders();

    public abstract boolean deleteData();

}
