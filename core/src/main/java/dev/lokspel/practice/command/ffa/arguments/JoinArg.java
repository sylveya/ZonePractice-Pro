package dev.lokspel.practice.command.ffa.arguments;

import dev.lokspel.practice.manager.arena.ArenaManager;
import dev.lokspel.practice.manager.arena.arenas.FFAArena;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.ffa.FFAManager;
import dev.lokspel.practice.manager.fight.ffa.game.FFA;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import dev.lokspel.practice.manager.profile.enums.ProfileStatus;
import dev.lokspel.practice.util.Common;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public final class JoinArg {

    private JoinArg() {}

    public static void run(Player player, String label, String[] args) {
        if (args.length > 2) {
            Common.sendMMMessage(player, LanguageManager.getString("FFA.COMMAND.JOIN.HELP").replace("%label%", label));
            return;
        }

        Profile profile = ProfileManager.getInstance().getProfile(player);
        if (!profile.getStatus().equals(ProfileStatus.LOBBY)) {
            Common.sendMMMessage(player, LanguageManager.getString("FFA.COMMAND.JOIN.CANT-JOIN-FFA"));
            return;
        }

        if (args.length < 2) {
            FFAManager.getInstance().getArenaSelectorGui().update();
            FFAManager.getInstance().getArenaSelectorGui().open(player);
            return;
        }

        FFAArena arena = ArenaManager.getInstance().getFFAArena(args[1]);
        if (arena == null) {
            Common.sendMMMessage(player, LanguageManager.getString("FFA.COMMAND.JOIN.ARENA-NOT-FOUND"));
            return;
        }

        FFA ffa = arena.getFfa();
        if (!arena.isEnabled() || ffa == null || !ffa.isOpen()) {
            Common.sendMMMessage(player, LanguageManager.getString("FFA.COMMAND.JOIN.ARENA-CLOSED").replace("%arena%", arena.getDisplayName()));
            return;
        }

        if (arena.getAssignedLadders().size() == 1) {
            NormalLadder ladder = arena.getAssignedLadders().iterator().next();
            player.closeInventory();
            ffa.addPlayer(player, ladder);
        } else {
            ffa.getLadderSelectorGui().update();
            ffa.getLadderSelectorGui().open(player);
        }
    }

    public static List<String> tabComplete(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        Profile profile = ProfileManager.getInstance().getProfile(player);
        if (args.length != 2 || !profile.getStatus().equals(ProfileStatus.LOBBY)) {
            return arguments;
        }

        for (FFAArena arena : ArenaManager.getInstance().getFFAArenas()) {
            FFA ffa = arena.getFfa();
            if (arena.isEnabled() && ffa != null && ffa.isOpen()) {
                arguments.add(arena.getName());
            }
        }

        return StringUtil.copyPartialMatches(args[1], arguments, new ArrayList<>());
    }

}
