package dev.lokspel.practice.manager.spectator;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.ffa.FFAManager;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.MatchManager;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.gui.guis.SpectatorMenuGui;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.StringUtil;
import dev.lokspel.practice.util.cooldown.CooldownObject;
import dev.lokspel.practice.util.cooldown.PlayerCooldown;
import dev.lokspel.practice.util.interfaces.Spectatable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class SpectatorManager {

    private static SpectatorManager instance;

    public static SpectatorManager getInstance() {
        if (instance == null)
            instance = new SpectatorManager();
        return instance;
    }

    private static final Random random = new Random();
    private final Map<Player, Spectatable> spectators = new HashMap<>();
    private final SpectatorMenuGui spectatorMenuGui;

    private SpectatorManager() {
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), AstralPractice.getInstance());

        this.spectatorMenuGui = (SpectatorMenuGui) GUIManager.getInstance().addGUI(new SpectatorMenuGui());
    }

    public void spectateMenuUse(Player player) {
        if (!player.hasPermission("zpp.spectate.menu")) {
            Common.sendMMMessage(player, LanguageManager.getString("SPECTATE.NO-PERMISSIONS"));
            return;
        }

        spectatorMenuGui.open(player);
    }

    public static void spectateRandomMatchItemUse(Player player) {
        if (!player.hasPermission("zpp.spectate.random")) {
            Common.sendMMMessage(player, LanguageManager.getString("SPECTATE.NO-PERMISSIONS"));
            return;
        }

        if (!player.hasPermission("zpp.bypass.cooldown") && PlayerCooldown.isActive(player, CooldownObject.RANDOM_MATCH)) {
            Common.sendMMMessage(player, StringUtil.replaceSecondString(LanguageManager.getString("SPECTATE.RANDOM-MATCH-COOLDOWN"), PlayerCooldown.getLeftInDouble(player, CooldownObject.RANDOM_MATCH)));
            return;
        }

        PlayerCooldown.addCooldown(player, CooldownObject.RANDOM_MATCH, ConfigManager.getInt("SPECTATOR-SETTINGS.RANDOM-MATCH-COOLDOWN"));
        spectateRandomMatch(player);
    }

    /**
     * Teleports a spectator to a single fighter of the match they are watching.
     * Does nothing if the spectator stopped watching that match in the meantime.
     */
    public void teleportToTarget(Player spectator, Match match, Player target) {
        if (this.spectators.get(spectator) != match) return;
        if (!match.getPlayers().contains(target)) return;

        spectator.teleport(target);
    }

    public static void spectateRandomMatch(Player player) {
        List<Spectatable> spectatables = new ArrayList<>(MatchManager.getInstance().getLiveMatches());
        spectatables.addAll(FFAManager.getInstance().getOpenFFAs());

        if (spectatables.isEmpty()) {
            Common.sendMMMessage(player, LanguageManager.getString("SPECTATE.NO-MATCH"));
            return;
        }

        Spectatable match = null;
        if (spectatables.size() > 1) {
            boolean found = false;
            do {
                Spectatable randomMatch = spectatables.get(random.nextInt(spectatables.size()));
                if (!randomMatch.getSpectators().contains(player)) {
                    found = true;
                    match = randomMatch;
                }
            } while (!found);
        } else {
            match = spectatables.getFirst();
        }

        if (match.getSpectators().contains(player)) {
            Common.sendMMMessage(player, LanguageManager.getString("SPECTATE.MATCH.ONLY-ONE-TO-SPECTATE"));
            return;
        }

        match.addSpectator(player, null, true, false);
    }

}
