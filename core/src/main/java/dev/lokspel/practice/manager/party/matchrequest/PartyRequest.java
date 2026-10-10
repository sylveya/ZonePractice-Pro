package dev.lokspel.practice.manager.party.matchrequest;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.arena.arenas.Arena;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.type.duel.Duel;
import dev.lokspel.practice.manager.fight.match.type.playersvsplayers.partyvsparty.PartyVsParty;
import dev.lokspel.practice.manager.ladder.abstraction.Ladder;
import dev.lokspel.practice.manager.ladder.util.LadderUtil;
import dev.lokspel.practice.manager.party.Party;
import dev.lokspel.practice.manager.party.PartyManager;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.playerutil.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PartyRequest {

    RequestManager requestManager = PartyManager.getInstance().getRequestManager();

    private final PartyRequest partyRequest;
    @Getter
    @Setter
    private Party sender;
    @Getter
    @Setter
    private Party target;
    @Getter
    @Setter
    private Ladder ladder;
    @Getter
    @Setter
    private Arena arena;
    @Getter
    @Setter
    private int rounds;

    public PartyRequest(Party sender, Party target, Ladder ladder, Arena arena, int rounds) {
        this.partyRequest = this;

        this.sender = sender;
        this.target = target;
        this.ladder = ladder;
        this.arena = arena;
        this.rounds = rounds;
    }

    public void sendRequest() {
        List<PartyRequest> partyRequests;

        if (requestManager.getRequests().containsKey(target))
            partyRequests = new ArrayList<>(requestManager.getRequests().get(target));
        else
            partyRequests = new ArrayList<>();

        partyRequests.removeIf(oldRequest -> oldRequest.getSender().equals(sender));

        partyRequests.add(this);
        requestManager.getRequests().put(target, partyRequests);

        sender.getLeader().closeInventory();
        sendRequestMessage();

        new BukkitRunnable() {
            @Override
            public void run() {
                requestManager.getRequests().get(target).remove(partyRequest);
            }
        }.runTaskLaterAsynchronously(AstralPractice.getInstance(), 20L * ConfigManager.getInt("PARTY.REQUEST-EXPIRY"));
    }

    public void sendRequestMessage() {
        String arenaName;
        if (arena != null) arenaName = arena.getDisplayName();
        else arenaName = LanguageManager.getString("PARTY.MATCH-REQUEST-MESSAGE.RANDOM-ARENA-NAME");

        for (String line : LanguageManager.getList("PARTY.MATCH-REQUEST-MESSAGE.SENDER")) {
            Common.sendMMMessage(sender.getLeader(), line
                    .replace("%ladder%", ladder.getDisplayName())
                    .replace("%arena%", arenaName)
                    .replace("%rounds%", String.valueOf(rounds))
                    .replace("%target_leader%", target.getLeader().getName())
                    .replace("%target_members%", PlayerUtil.getPlayerNames(target.getMembers()).toString().replace("[", "").replace("]", ""))
            );
        }

        for (String line : LanguageManager.getList("PARTY.MATCH-REQUEST-MESSAGE.TARGET")) {
            Common.sendMMMessage(target.getLeader(), line
                    .replace("%ladder%", ladder.getDisplayName())
                    .replace("%arena%", arenaName)
                    .replace("%rounds%", String.valueOf(rounds))
                    .replace("%sender_leader%", sender.getLeader().getName())
                    .replace("%sender_members%", PlayerUtil.getPlayerNames(sender.getMembers()).toString().replace("[", "").replace("]", ""))
            );
        }
    }

    public void acceptRequest() {
        requestManager.getRequests().get(target).remove(this);

        Arena arena;
        if (this.getArena() != null) {
            if (this.getArena().getAvailableArena() != null) {
                arena = this.getArena();
            } else {
                Common.sendMMMessage(sender.getLeader(), LanguageManager.getString("PARTY.ARENA-BUSY"));
                arena = LadderUtil.getAvailableArena(ladder);
            }
        } else
            arena = LadderUtil.getAvailableArena(ladder);

        if (arena != null && arena.getAvailableArena() != null) {
            List<Player> matchPlayers = new ArrayList<>();
            matchPlayers.addAll(sender.getMembers());
            matchPlayers.addAll(target.getMembers());

            Match match;
            if (matchPlayers.size() == 2) {
                match = new Duel(ladder, arena, matchPlayers, false, rounds);
            } else {
                match = new PartyVsParty(ladder, arena, sender, target, matchPlayers, rounds);
            }

            sender.setMatch(match);
            target.setMatch(match);
            match.startMatch();
        } else {
            sender.sendMessage(LanguageManager.getString("PARTY.NO-AVAILABLE-ARENA"));
            target.sendMessage(LanguageManager.getString("PARTY.NO-AVAILABLE-ARENA"));
        }
    }

}
