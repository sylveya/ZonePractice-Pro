package dev.lokspel.practice.manager.inventory.service;

import dev.lokspel.practice.manager.fight.util.PlayerUtil;
import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.fight.match.MatchManager;
import dev.lokspel.practice.manager.inventory.Inventory;
import dev.lokspel.practice.manager.inventory.InventoryManager;
import dev.lokspel.practice.manager.inventory.InventoryUtil;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import dev.lokspel.practice.manager.server.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class InventoryTransitionService {

    private final InventoryManager inventoryManager;

    public InventoryTransitionService(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void setLobbyInventory(Player player, boolean teleport) {
        // The old match cleans players up with a delay, so a player may already be
        // in a new match by the time we get here. If so, don't send them back to the
        // lobby - otherwise they keep lobby items and can't fight in the new match.
        if (MatchManager.getInstance().getLiveMatchByPlayer(player) != null) {
            return;
        }

        Profile profile = ProfileManager.getInstance().getProfile(player);
        profile.setStatus(ProfileStatus.LOBBY);
        PlayerUtil.resetAttackSpeed(player);

        dev.lokspel.practice.util.playerutil.PlayerUtil.clearPlayer(player, false, profile.isFlying(), true);

        if (ZonePractice.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTask(ZonePractice.getInstance(), () -> InventoryUtil.setLobbyNametag(player, profile));
        } else {
            InventoryUtil.setLobbyNametag(player, profile);
        }

        if (teleport) {
            player.closeInventory();
        }

        if (profile.isStaffMode()) {
            inventoryManager.setStaffModeInventory(player);
        } else if (profile.isSpectatorMode()) {
            inventoryManager.setInventory(player, Inventory.InventoryType.SPEC_MODE_LOBBY);
        } else if (profile.isParty()) {
            inventoryManager.setInventory(player, Inventory.InventoryType.PARTY);
        } else {
            inventoryManager.setInventory(player, Inventory.InventoryType.LOBBY);
        }

        if (teleport && ServerManager.getLobby() != null) {
            player.teleport(ServerManager.getLobby());
        }

        player.updateInventory();
        inventoryManager.applyLobbyCosmetics(player);
    }

    public void setMatchQueueInventory(Player player, boolean closeInventory) {
        ProfileManager.getInstance().getProfile(player).setStatus(ProfileStatus.QUEUE);

        if (closeInventory) {
            player.closeInventory();
        }

        inventoryManager.setInventory(player, Inventory.InventoryType.MATCH_QUEUE);
    }

    public void setEventQueueInventory(Player player) {
        player.closeInventory();
        inventoryManager.setInventory(player, Inventory.InventoryType.EVENT_QUEUE);
    }

    public void setStaffModeInventory(Player player) {
        Profile profile = ProfileManager.getInstance().getProfile(player);
        profile.setStatus(ProfileStatus.STAFF_MODE);
        profile.setStaffMode(true);

        dev.lokspel.practice.util.playerutil.PlayerUtil.clearPlayer(player, false, player.hasPermission("zpp.staffmode.fly"), false);
        inventoryManager.setInventory(player, Inventory.InventoryType.STAFF_MODE);
    }
}


