package dev.lokspel.practice.manager.ladder.type;

import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.ladder.enums.LadderType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Creeper extends NormalLadder {

    private double creeperExplosionDelay;

    public Creeper(String name, LadderType type) {
        super(name, type);
    }

    public int getCreeperExplosionDelayTicks() {
        return (int) Math.round(creeperExplosionDelay * 20);
    }

}