package dev.lokspel.practice.manager.gui.guis;

import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.enums.TeamEnum;
import dev.lokspel.practice.manager.fight.match.interfaces.Team;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIItem;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.spectator.SpectatorManager;
import dev.lokspel.practice.util.InventoryUtil;
import dev.lokspel.practice.util.ItemCreateUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SpectatorTargetsGui extends GUI {

    private static final String GUI_PATH = "GUIS.SPECTATOR-TARGETS";

    private final Match match;
    private final Map<Integer, Player> headSlots = new HashMap<>();

    public SpectatorTargetsGui(Match match) {
        super(GUIType.Spectator_Targets);

        this.match = match;

        this.gui.put(1, InventoryUtil.createInventory(GUIFile.getString(GUI_PATH + ".TITLE"), getRows(match)));

        build();
    }

    private static int getRows(Match match) {
        int rows = (int) Math.ceil(match.getPlayers().size() / 9.0);
        return Math.clamp(rows, 1, 6);
    }

    @Override
    public void build() {
        Inventory inventory = this.gui.get(1);

        inventory.clear();
        headSlots.clear();

        int slot = 0;
        for (Player target : match.getPlayers()) {
            if (slot >= inventory.getSize()) break;

            headSlots.put(slot, target);
            inventory.setItem(slot, getTargetItem(target));
            slot++;
        }
    }

    // A new gui is built on every open, so there is nothing to update here.
    @Override
    public void update() {
    }

    private ItemStack getTargetItem(Player target) {
        GUIItem configItem = GUIFile.getGuiItem(GUI_PATH + ".ICONS.TARGET-ITEM");

        GUIItem headItem = new GUIItem(ItemCreateUtil.getPlayerHead(target));
        headItem.setName(configItem.getName());
        headItem.setLore(configItem.getLore());

        return headItem
                .replace("%player%", target.getName())
                .replace("%team%", getTeam(target).getNameMM())
                .replace("%health%", String.format("%.1f", target.getHealth()))
                .get();
    }

    private TeamEnum getTeam(Player target) {
        return match instanceof Team team ? team.getTeam(target) : TeamEnum.FFA;
    }

    @Override
    public void handleClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player spectator = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        if (slot >= gui.get(1).getSize()) return;

        Player target = headSlots.get(slot);
        if (target == null) return;

        SpectatorManager.getInstance().teleportToTarget(spectator, match, target);
        spectator.closeInventory();
    }

}
