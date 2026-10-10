package dev.lokspel.practice.manager.fight.ffa;

import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.arenas.FFAArena;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.ffa.game.FFA;
import dev.lokspel.practice.manager.fight.ffa.game.FFAArenaSelectorGui;
import dev.lokspel.practice.manager.gui.GUIManager;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import dev.lokspel.practice.manager.spectator.SpectatorManager;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.interfaces.Spectatable;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


@Getter
public class FFAManager {

    private static FFAManager instance;

    public static FFAManager getInstance() {
        if (instance == null)
            instance = new FFAManager();
        return instance;
    }

    private final FFAArenaSelectorGui arenaSelectorGui;

    private FFAManager() {
        this.arenaSelectorGui = new FFAArenaSelectorGui();
        GUIManager.getInstance().addGUI(this.arenaSelectorGui);
    }

    /**
     * Returns true if the player joins the arena or its ladder selector opens.
     */
    public boolean joinArena(Player player, FFAArena arena) {
        Profile profile = ProfileManager.getInstance().getProfile(player);
        if (!profile.getStatus().equals(ProfileStatus.LOBBY)) {
            Common.sendMMMessage(player, LanguageManager.getString("FFA.COMMAND.JOIN.CANT-JOIN-FFA"));
            return false;
        }

        if (arena == null) {
            Common.sendMMMessage(player, LanguageManager.getString("FFA.COMMAND.JOIN.ARENA-NOT-FOUND"));
            return false;
        }

        FFA ffa = arena.getFfa();
        if (!arena.isEnabled() || ffa == null || !ffa.isOpen()) {
            Common.sendMMMessage(player, LanguageManager.getString("FFA.COMMAND.JOIN.ARENA-CLOSED").replace("%arena%", arena.getDisplayName()));
            return false;
        }

        if (arena.getAssignedLadders().size() == 1) {
            NormalLadder ladder = arena.getAssignedLadders().iterator().next();
            player.closeInventory();
            ffa.addPlayer(player, ladder);
        } else {
            ffa.getLadderSelectorGui().update();
            ffa.getLadderSelectorGui().open(player);
        }

        return true;
    }

    public List<FFA> getOpenFFAs() {
        List<FFA> ffas = new ArrayList<>();
        for (FFAArena ffaArena : ArenaManager.getInstance().getFFAArenas())
            if (ffaArena.getFfa().isOpen())
                ffas.add(ffaArena.getFfa());
        return ffas;
    }

    public void endFFAs() {
        for (FFAArena ffaArena : ArenaManager.getInstance().getFFAArenas())
            ffaArena.getFfa().close("");
    }

    public FFA getFFAByPlayer(Player player) {
        for (FFAArena ffaArena : ArenaManager.getInstance().getFFAArenas()) {
            FFA ffa = ffaArena.getFfa();
            if (ffa.getPlayers().containsKey(player)) {
                return ffa;
            }

            // Fallback for stale Player-object map keys.
            for (Player ffaPlayer : ffa.getPlayers().keySet()) {
                if (player.getUniqueId().equals(ffaPlayer.getUniqueId())) {
                    return ffa;
                }
            }
        }
        return null;
    }

    public FFA getFFABySpectator(Player player) {
        Spectatable spectatable = SpectatorManager.getInstance().getSpectators().get(player);
        if (spectatable instanceof FFA)
            return (FFA) spectatable;
        else
            return null;
    }

}
