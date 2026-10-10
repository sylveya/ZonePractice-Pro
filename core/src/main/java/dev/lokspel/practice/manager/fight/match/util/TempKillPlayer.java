package dev.lokspel.practice.manager.fight.match.util;

import dev.lokspel.practice.manager.fight.util.PlayerUtil;
import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.Round;
import dev.lokspel.practice.manager.fight.match.enums.TeamEnum;
import dev.lokspel.practice.manager.fight.match.interfaces.Team;
import dev.lokspel.practice.manager.ladder.abstraction.Ladder;
import dev.lokspel.practice.manager.ladder.abstraction.interfaces.RespawnableLadder;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.StringUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static dev.lokspel.practice.manager.fight.match.util.TeamUtil.replaceTeamNames;

public class TempKillPlayer extends BukkitRunnable {

    private final String languagePath;
    @Getter
    private boolean running = false;

    private final Match match;
    private final Round round;

    @Getter
    private final Player player;
    private final TeamEnum playerTeam;
    private int respawnTime;

    public TempKillPlayer(final Round round, final Player player, final int respawnTime) {
        this.round = round;
        this.match = round.getMatch();

        this.player = player;
        this.respawnTime = respawnTime;

        if (match instanceof Team)
            playerTeam = ((Team) match).getTeam(player);
        else
            playerTeam = TeamEnum.TEAM1;

        // Use the ladder's language path method
        this.languagePath = resolveLanguagePath(match.getLadder());

        this.begin();
    }

    /**
     * Resolves the language path for respawn messages from the ladder.
     * Uses the RespawnableLadder interface.
     */
    private String resolveLanguagePath(Ladder ladder) {
        String matchTypePath = "MATCH." + match.getType().getPathName() + ".LADDER-SPECIFIC.";

        if (ladder instanceof RespawnableLadder respawnableLadder) {
            String basePath = respawnableLadder.getRespawnLanguagePath();
            if (basePath != null && !basePath.equals("MATCH.RESPAWN")) {
                return matchTypePath + basePath;
            }
        }
        return null;
    }

    public void begin() {
        if (round.getTempKill(player) != null) return;
        if (running) return;

        /*
         * Battle rush remove blocks so the players don't get them unnecessarily
         */
        for (var entry : match.getFightChange().getBlocks().values()) {
            if (entry.getTempData() != null && entry.getTempData().getPlayer().equals(player)) {
                entry.getTempData().setReturnItem(false);
            }
        }

        round.getTempDead().add(this);
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);

        running = true;
        this.runTaskTimer(ZonePractice.getInstance(), 0, 20L);
    }

    public void cancel(boolean setPlayer) {
        if (!running) return;

        Bukkit.getScheduler().cancelTask(this.getTaskId());
        running = false;

        round.getTempDead().remove(this);

        if (!setPlayer) return;
        if (!match.getPlayers().contains(player)) return;

        if (languagePath != null && respawnTime > 0)
            match.sendMessage(replaceTeamNames(LanguageManager.getString(languagePath + ".PLAYER-RESPAWNED"), player, playerTeam), true);

        // Clear spectator remnants immediately so damage/projectile listeners no longer treat the player as protected.
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        PlayerUtil.setCollidesWithEntities(player, true);

        match.teleportPlayer(player);
        dev.lokspel.practice.util.playerutil.PlayerUtil.setFightPlayer(player, match.getLadder());
        match.getMatchPlayers().get(player).setKitChooserOrKit(playerTeam);
    }

    @Override
    public void run() {
        if (respawnTime == 0) {
            cancel(true);
            return;
        }

        if (languagePath != null)
            Common.sendMMMessage(player, StringUtil.replaceSecondString(
                    replaceTeamNames(LanguageManager.getString(languagePath + ".RESPAWN"), player, playerTeam),
                    respawnTime));

        respawnTime--;
    }

}
