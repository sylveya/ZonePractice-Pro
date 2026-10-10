package dev.lokspel.practice.manager.fight.match.type.playersvsplayers;

import dev.lokspel.practice.manager.fight.match.Match;
import dev.lokspel.practice.manager.fight.match.Round;
import dev.lokspel.practice.manager.fight.match.enums.TeamEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class PlayersVsPlayersRound extends Round {

    protected TeamEnum roundWinner;

    protected PlayersVsPlayersRound(Match match, int roundNumber) {
        super(match, roundNumber);
    }

    @Override
    public PlayersVsPlayers getMatch() {
        return (PlayersVsPlayers) this.match;
    }

}
