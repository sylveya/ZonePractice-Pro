package dev.lokspel.practice.manager.ladder.enums;

import dev.lokspel.practice.manager.backend.ConfigManager;
import lombok.Getter;

public enum KnockbackType {

    DEFAULT
            (
                    0,
                    0,
                    0,
                    0,
                    -1,
                    -1,
                    -1,
                    -1
            ),
    NORMAL
            (
                    ConfigManager.getDouble("KNOCKBACK.NORMAL.AIR-HORIZONTAL"),
                    ConfigManager.getDouble("KNOCKBACK.NORMAL.AIR-VERTICAL"),
                    ConfigManager.getDouble("KNOCKBACK.NORMAL.HORIZONTAL"),
                    ConfigManager.getDouble("KNOCKBACK.NORMAL.VERTICAL"),
                    readMax("KNOCKBACK.NORMAL.MAX-AIR-HORIZONTAL", -1.0D),
                    readMax("KNOCKBACK.NORMAL.MAX-AIR-VERTICAL", -1.0D),
                    readMax("KNOCKBACK.NORMAL.MAX-HORIZONTAL", -1.0D),
                    readMax("KNOCKBACK.NORMAL.MAX-VERTICAL", -1.0D)
            ),
    COMBO
            (
                    ConfigManager.getDouble("KNOCKBACK.COMBO.AIR-HORIZONTAL"),
                    ConfigManager.getDouble("KNOCKBACK.COMBO.AIR-VERTICAL"),
                    ConfigManager.getDouble("KNOCKBACK.COMBO.HORIZONTAL"),
                    ConfigManager.getDouble("KNOCKBACK.COMBO.VERTICAL"),
                    readMax("KNOCKBACK.COMBO.MAX-AIR-HORIZONTAL", 1.35D),
                    readMax("KNOCKBACK.COMBO.MAX-AIR-VERTICAL", 0.62D),
                    readMax("KNOCKBACK.COMBO.MAX-HORIZONTAL", 1.15D),
                    readMax("KNOCKBACK.COMBO.MAX-VERTICAL", 0.52D)
            );

    @Getter
    private final double airhorizontal;
    @Getter
    private final double airvertical;
    @Getter
    private final double horizontal;
    @Getter
    private final double vertical;
    @Getter
    private final double maxAirhorizontal;
    @Getter
    private final double maxAirvertical;
    @Getter
    private final double maxHorizontal;
    @Getter
    private final double maxVertical;

    KnockbackType(double airhorizontal, double airvertical, double horizontal, double vertical,
                  double maxAirhorizontal, double maxAirvertical, double maxHorizontal, double maxVertical) {
        this.airhorizontal = airhorizontal;
        this.airvertical = airvertical;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.maxAirhorizontal = maxAirhorizontal;
        this.maxAirvertical = maxAirvertical;
        this.maxHorizontal = maxHorizontal;
        this.maxVertical = maxVertical;
    }

    private static double readMax(String path, double defaultValue) {
        double configured = ConfigManager.getConfig().getDouble(path, defaultValue);
        return configured > 0.0D ? configured : -1.0D;
    }

}
