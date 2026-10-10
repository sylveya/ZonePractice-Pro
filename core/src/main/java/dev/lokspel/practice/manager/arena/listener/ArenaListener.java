package dev.lokspel.practice.manager.arena.listener;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.arenas.Arena;
import dev.lokspel.practice.manager.arena.arenas.interfaces.BasicArena;
import dev.lokspel.practice.manager.arena.util.ArenaUtil;
import dev.lokspel.practice.manager.arena.util.ArenaWorldUtil;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.match.MatchManager;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.server.ServerManager;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import static dev.lokspel.practice.manager.arena.ArenaManager.LOADED_CHUNK_KEYS;
import static dev.lokspel.practice.manager.arena.ArenaManager.LOAD_CHUNKS;

public class ArenaListener implements Listener {

    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        World world = player.getWorld();

        if (world.equals(ArenaWorldUtil.getArenasCopyWorld())) {
            e.setCancelled(true);
        } else if (world.equals(ArenaWorldUtil.getArenasWorld())) {
            e.setCancelled(true);
        }
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Profile profile = ProfileManager.getInstance().getProfile(player);

        switch (profile.getStatus()) {
            case MATCH:
            case FFA:
            case EVENT:
            case SPECTATE:
                return;
        }

        World playerWorld = player.getWorld();
        Location blockLoc = e.getBlock().getLocation();

        if (playerWorld.equals(ArenaWorldUtil.getArenasCopyWorld())) {
            Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-COPIES"));
            e.setCancelled(true);
        } else if (playerWorld.equals(ArenaWorldUtil.getArenasWorld())) {
            for (Cuboid cuboid : ArenaManager.getInstance().getArenaCuboids().keySet()) {
                if (cuboid.contains(blockLoc)) {
                    BasicArena arena = ArenaManager.getInstance().getArenaCuboids().get(cuboid);
                    Arena mainArena = ArenaUtil.getArena(arena);
                    if (mainArena == null)
                        return;

                    if (mainArena.isBuild() && !mainArena.getCopies().isEmpty()) {
                        e.setCancelled(true);
                        Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-ARENA-WITH-COPIES").replace("%arena%", mainArena.getDisplayName()));
                    } else if (!MatchManager.getInstance().getLiveMatchesByArena(arena).isEmpty()) {
                        e.setCancelled(true);
                        Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-ARENA-WITH-MATCH").replace("%arena%", mainArena.getDisplayName()));
                    }
                }
            }
        }
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Profile profile = ProfileManager.getInstance().getProfile(player);

        switch (profile.getStatus()) {
            case MATCH:
            case FFA:
            case EVENT:
            case SPECTATE:
                return;
        }

        World playerWorld = player.getWorld();
        Location blockLoc = e.getBlock().getLocation();

        if (playerWorld.equals(ArenaWorldUtil.getArenasCopyWorld())) {
            Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-COPIES"));
            e.setCancelled(true);
        } else if (playerWorld.equals(ArenaWorldUtil.getArenasWorld())) {
            for (Cuboid cuboid : ArenaManager.getInstance().getArenaCuboids().keySet()) {
                if (cuboid.contains(blockLoc)) {
                    BasicArena arena = ArenaManager.getInstance().getArenaCuboids().get(cuboid);
                    Arena mainArena = ArenaUtil.getArena(arena);
                    if (mainArena == null)
                        return;

                    if (mainArena.isBuild() && !mainArena.getCopies().isEmpty()) {
                        e.setCancelled(true);
                        Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-ARENA-WITH-COPIES").replace("%arena%", mainArena.getDisplayName()));
                    } else if (!MatchManager.getInstance().getLiveMatchesByArena(arena).isEmpty()) {
                        e.setCancelled(true);
                        Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-ARENA-WITH-MATCH").replace("%arena%", mainArena.getDisplayName()));
                    }
                }
            }
        }
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Profile profile = ProfileManager.getInstance().getProfile(player);
        Action action = e.getAction();

        switch (profile.getStatus()) {
            case MATCH:
            case FFA:
            case EVENT:
            case SPECTATE:
                return;
        }

        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.LEFT_CLICK_BLOCK)) return;

        World playerWorld = player.getWorld();
        Location blockLoc = e.getClickedBlock().getLocation();

        if (playerWorld.equals(ArenaWorldUtil.getArenasCopyWorld())) {
            Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-COPIES"));
            e.setCancelled(true);
        } else if (playerWorld.equals(ArenaWorldUtil.getArenasWorld())) {
            for (Cuboid cuboid : ArenaManager.getInstance().getArenaCuboids().keySet()) {
                if (cuboid.contains(blockLoc)) {
                    BasicArena arena = ArenaManager.getInstance().getArenaCuboids().get(cuboid);
                    Arena mainArena = ArenaUtil.getArena(arena);
                    if (mainArena == null)
                        return;

                    if (mainArena.isBuild() && !mainArena.getCopies().isEmpty()) {
                        e.setCancelled(true);
                        Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-ARENA-WITH-COPIES").replace("%arena%", mainArena.getDisplayName()));
                    } else if (!MatchManager.getInstance().getLiveMatchesByArena(arena).isEmpty()) {
                        e.setCancelled(true);
                        Common.sendMMMessage(player, LanguageManager.getString("ARENA.CANT-EDIT-ARENA-WITH-MATCH").replace("%arena%", mainArena.getDisplayName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (LOAD_CHUNKS) {
            long chunkKey = ArenaUtil.chunkKey(e.getChunk().getX(), e.getChunk().getZ());
            if (LOADED_CHUNK_KEYS.contains(chunkKey)) {
                // Use addPluginChunkTicket to force-keep the chunk loaded.
                // This is safe from recursion (unlike getChunkAtAsync which can
                // trigger chunk scheduling → more unloads → StackOverflowError).
                e.getChunk().addPluginChunkTicket(AstralPractice.getInstance());
            }
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        if (e.getWorld() == ArenaWorldUtil.getArenasWorld()) {
            e.setCancelled(true);
            return;
        }

        if (e.getWorld() == ArenaWorldUtil.getArenasCopyWorld()) {
            e.setCancelled(true);
            return;
        }

        if (ServerManager.getLobby() != null && ServerManager.getLobby().getWorld() == e.getWorld()) {
            e.setCancelled(true);
        }
    }

}
