package dev.nandi0813.practice.manager.party;

import dev.nandi0813.practice.manager.backend.ConfigManager;
import dev.nandi0813.practice.manager.backend.LanguageManager;
import dev.nandi0813.practice.manager.fight.match.Match;
import dev.nandi0813.practice.manager.fight.match.enums.MatchStatus;
import dev.nandi0813.practice.manager.gui.GUIManager;
import dev.nandi0813.practice.manager.gui.GUIType;
import dev.nandi0813.practice.manager.gui.guis.party.PartySettingsGui;
import dev.nandi0813.practice.manager.inventory.InventoryManager;
import dev.nandi0813.practice.manager.profile.Profile;
import dev.nandi0813.practice.manager.profile.ProfileManager;
import dev.nandi0813.practice.manager.profile.enums.ProfileStatus;
import dev.nandi0813.practice.util.Common;
import dev.nandi0813.practice.util.playerutil.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Party implements dev.nandi0813.api.Interface.Party {

    private static final boolean DEFAULT_PUBLIC_PARTY = ConfigManager.getBoolean("PARTY.SETTINGS.DEFAULT.PUBLIC-PARTY");
    private static final boolean DEFAULT_ALL_INVITE = ConfigManager.getBoolean("PARTY.SETTINGS.DEFAULT.ALL-INVITE");
    private static final boolean DEFAULT_PARTY_CHAT = ConfigManager.getBoolean("PARTY.SETTINGS.DEFAULT.PARTY-CHAT");
    private static final boolean DEFAULT_DUEL_REQUEST = ConfigManager.getBoolean("PARTY.SETTINGS.DEFAULT.DUEL-REQUESTS");

    @Setter
    private Player leader;
    private final List<Player> members = new ArrayList<>();
    private final Map<Player, Long> invites = new HashMap<>();

    @Setter
    private BroadcastTask broadcastTask = new BroadcastTask(this);
    private final PartySettingsGui partySettingsGui;

    @Setter
    private int maxPlayerLimit;
    @Setter
    private boolean publicParty;
    @Setter
    private boolean broadcastParty;
    @Setter
    private boolean allInvite;
    @Setter
    private boolean partyChat;
    @Setter
    private boolean duelRequests;

    private Match match;

    /**
     * Sets the party's current match.
     * <p>
     * If the party already had a match that is still running, it is ended first.
     * Otherwise the old match would stay registered among the live matches
     * (inflating the "in fights" count) and its arena would never roll back.
     *
     * @param match the new match, or null to clear the current one
     */
    public void setMatch(Match match) {
        if (match != null && this.match != null && !this.match.equals(match)
                && PartyManager.getInstance().hasActiveMatch(this)
                && this.match.getStatus() != MatchStatus.END
                && this.match.getStatus() != MatchStatus.OVER) {

            this.match.sendMessage(LanguageManager.getString("PARTY.PREVIOUS-MATCH-ENDED"), true);
            this.match.endMatch();
        }

        this.match = match;
    }

    public Party(Player owner) {
        this.leader = owner;
        this.members.add(owner);

        this.maxPlayerLimit = PartyManager.getInstance().resolvePartyMemberLimit(owner);
        this.publicParty = DEFAULT_PUBLIC_PARTY;
        this.broadcastParty = DEFAULT_PUBLIC_PARTY;
        this.allInvite = DEFAULT_ALL_INVITE;
        this.partyChat = DEFAULT_PARTY_CHAT;
        this.duelRequests = DEFAULT_DUEL_REQUEST;

        this.partySettingsGui = (PartySettingsGui) GUIManager.getInstance().addGUI(new PartySettingsGui(this));
    }

    public void setNewOwner(Player newOwner) {
        if (broadcastTask.isRunning())
            broadcastTask.cancel();

        leader = newOwner;
        refreshMaxPlayerLimitForLeader();
        sendMessage(LanguageManager.getString("PARTY.NEW-LEADER").replace("%player%", newOwner.getName()));
    }

    public void refreshMaxPlayerLimitForLeader() {
        int leaderLimit = PartyManager.getInstance().resolvePartyMemberLimit(leader);
        this.maxPlayerLimit = Math.max(members.size(), leaderLimit);

        if (members.size() >= this.maxPlayerLimit && isBroadcastParty()) {
            broadcastTask.cancel();
        }
    }

    public void addMember(Player member) {
        Profile memberProfile = ProfileManager.getInstance().getProfile(member);

        members.add(member);
        memberProfile.setParty(true);
        InventoryManager.getInstance().setLobbyInventory(member, false);

        sendMessage(LanguageManager.getString("PARTY.PLAYER-JOINED").replace("%player%", member.getName()));
        GUIManager.getInstance().searchGUI(GUIType.Party_OtherParties).update();
        GUIManager.getInstance().searchGUI(GUIType.Party_PublicParties).update();

        // If the party is full, stop broadcasting it.
        if (members.size() == maxPlayerLimit && isBroadcastParty())
            broadcastTask.cancel();
    }

    public void removeMember(Player member, boolean kick) {
        Profile memberProfile = ProfileManager.getInstance().getProfile(member);

        if (kick)
            sendMessage(LanguageManager.getString("PARTY.PLAYER-KICKED").replace("%player%", member.getName()));
        else
            sendMessage(LanguageManager.getString("PARTY.PLAYER-LEFT").replace("%player%", member.getName()));

        if (member.equals(leader))
            disband();

        members.remove(member);
        memberProfile.setParty(false);
        memberProfile.setPartyChat(false);

        if (memberProfile.getStatus().equals(ProfileStatus.LOBBY))
            InventoryManager.getInstance().setLobbyInventory(member, false);

        GUIManager.getInstance().searchGUI(GUIType.Party_OtherParties).update();
        GUIManager.getInstance().searchGUI(GUIType.Party_PublicParties).update();
    }

    public void disband() {
        broadcastTask.cancel();
        sendMessage(LanguageManager.getString("PARTY.PARTY-DISBANDED"));

        for (Player member : members) {
            Profile memberProfile = ProfileManager.getInstance().getProfile(member);
            memberProfile.setParty(false);
            memberProfile.setPartyChat(false);
        }

        List<Player> members_copy = new ArrayList<>(members);
        members.clear();

        if (match != null) {
            if (ConfigManager.getBoolean("PARTY.END-MATCH-ON-DISBAND")) {
                match.sendMessage(LanguageManager.getString("PARTY.MATCH-END"), true);
                match.endMatch();
            }
        } else {
            for (Player member : members_copy)
                InventoryManager.getInstance().setLobbyInventory(member, false);
        }

        GUIManager.getInstance().searchGUI(GUIType.Party_OtherParties).update();
        GUIManager.getInstance().searchGUI(GUIType.Party_PublicParties).update();
        PartyManager.getInstance().getParties().remove(this);
    }

    public boolean canUsePartyChat(Player player) {
        return partyChat || leader.equals(player);
    }

    public void sendMessage(String message) {
        for (Player player : members)
            Common.sendMMMessage(player, message);
    }

    public List<String> getMemberNames() {
        return PlayerUtil.getPlayerNames(members);
    }

}
