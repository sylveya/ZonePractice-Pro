package dev.lokspel.practice.manager.fight.match.enums;

import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.util.Common;

public enum WeightClass {

    UNRANKED(ConfigManager.getConfig().getString("MATCH-SETTINGS.WEIGHT-CLASS.UNRANKED")),
    RANKED(ConfigManager.getConfig().getString("MATCH-SETTINGS.WEIGHT-CLASS.RANKED"));

    private final String name;

    WeightClass(String name) {
        this.name = name;
    }

    public String getName() {
        return Common.mmToNormal(this.name);
    }

    public String getMMName() {
        return Common.serializeNormalToMMString(this.name);
    }

}
