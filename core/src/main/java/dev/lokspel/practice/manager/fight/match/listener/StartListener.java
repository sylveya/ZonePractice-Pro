package dev.lokspel.practice.manager.fight.match.listener;

import dev.lokspel.api.Event.Match.MatchRoundStartEvent;
import dev.lokspel.api.Event.Match.MatchStartEvent;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.server.ServerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class StartListener implements Listener {

    @EventHandler
    public void onMatchStart(MatchStartEvent e) {
        Match match = (Match) e.getMatch();

        if (!(match.getLadder() instanceof NormalLadder ladder)) {
            return;
        }

        String path = "MATCH-SETTINGS.START-COMMAND." + ladder.getName().toUpperCase() + ".MATCH";
        if (ConfigManager.getConfig().isList(path)) {
            List<String> commands = ConfigManager.getConfig().getStringList(path);
            for (String command : commands) {
                e.getMatch().getPlayers().forEach(player -> ServerManager.runConsoleCommand(command.replace("%player%", player.getName())));
            }
        }
    }

    @EventHandler
    public void onMatchRoundStart(MatchRoundStartEvent e) {
        Match match = (Match) e.getMatch();

        if (!(match.getLadder() instanceof NormalLadder ladder)) {
            return;
        }

        String path = "MATCH-SETTINGS.START-COMMAND." + ladder.getName().toUpperCase() + ".ROUND";
        if (ConfigManager.getConfig().isList(path)) {
            List<String> commands = ConfigManager.getConfig().getStringList(path);
            for (String command : commands) {
                e.getMatch().getPlayers().forEach(player -> ServerManager.runConsoleCommand(command.replace("%player%", player.getName())));
            }
        }
    }

}
