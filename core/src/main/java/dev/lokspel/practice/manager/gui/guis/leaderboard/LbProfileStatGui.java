package dev.lokspel.practice.manager.gui.guis.leaderboard;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.backend.GUIFile;
import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.ladder.LadderManager;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LbProfileStatGui extends GUI {

    private final Profile profile;
    private final GUI backTo;

    public LbProfileStatGui(Profile profile, GUI backTo) {
        super(GUIType.Leaderboard_Profile);
        this.profile = profile;
        this.backTo = backTo;

        this.gui.put(1, InventoryUtil.createInventory(GUIFile.getString("GUIS.STATISTICS.PLAYER-STATISTICS.TITLE").replace("%player%", profile.getPlayer().getName()), 6));

        build();
    }

    @Override
    public void build() {
        update();
    }

    @Override
    public void update() {
        Bukkit.getScheduler().runTaskAsynchronously(ZonePractice.getInstance(), () ->
        {
            Inventory inventory = gui.get(1);
            inventory.clear();

            for (int i = 45; i < 54; i++)
                inventory.setItem(i, GUIManager.getFILLER_ITEM());

            for (NormalLadder ladder : LadderManager.getInstance().getLadders()) {
                if (!ladder.isEnabled()) continue;

                int slot = inventory.firstEmpty();
                if (slot == -1) break;
                inventory.setItem(slot, LbGuiUtil.createLadderStatItem(profile, ladder).get());
            }

            ItemStack fillerItem = GUIFile.getGuiItem("GUIS.STATISTICS.PLAYER-STATISTICS.ICONS.FILLER-ITEM").get();
            for (int i = 0; i < 45; i++) {
                ItemStack current = inventory.getItem(i);
                if (current == null || current.getType().equals(Material.AIR))
                    inventory.setItem(i, fillerItem);
            }

            inventory.setItem(45, GUIFile.getGuiItem("GUIS.STATISTICS.PLAYER-STATISTICS.ICONS.BACK-TO-HUB").get());
            inventory.setItem(49, LbGuiUtil.createProfileAllStatItem(profile));

            updatePlayers();
        });
    }

    @Override
    public void handleClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        e.setCancelled(true);

        if (slot == 45) {
            if (backTo != null) backTo.open(player);
            else player.closeInventory();
        }
    }

}
