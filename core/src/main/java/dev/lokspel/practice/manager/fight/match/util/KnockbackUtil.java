package dev.lokspel.practice.manager.fight.match.util;

import dev.lokspel.practice.manager.ladder.enums.KnockbackType;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public enum KnockbackUtil {
    ;

    public static void setPlayerKnockback(Entity target, Entity attacker, KnockbackType knockbackType) {
        Vector currentVelocity = target.getVelocity().clone();
        boolean targetOnGround = target.isOnGround();

        double horizontalScale = targetOnGround
                ? knockbackType.getHorizontal()
                : knockbackType.getAirhorizontal();
        double verticalScale = targetOnGround
                ? knockbackType.getVertical()
                : knockbackType.getAirvertical();
        double maxHorizontal = targetOnGround
                ? knockbackType.getMaxHorizontal()
                : knockbackType.getMaxAirhorizontal();
        double maxVertical = targetOnGround
                ? knockbackType.getMaxVertical()
                : knockbackType.getMaxAirvertical();

        Vector awayFromAttacker = target.getLocation().toVector().subtract(attacker.getLocation().toVector());
        awayFromAttacker.setY(0);

        // Fallback for overlapping hitboxes where distance can be ~0.
        if (awayFromAttacker.lengthSquared() < 1.0E-6D) {
            awayFromAttacker = attacker.getLocation().getDirection().multiply(-1);
            awayFromAttacker.setY(0);
        }

        if (awayFromAttacker.lengthSquared() < 1.0E-6D) {
            return;
        }

        awayFromAttacker.normalize();

        double horizontalMagnitude = Math.hypot(currentVelocity.getX(), currentVelocity.getZ());
        if (horizontalMagnitude < 0.08D) {
            horizontalMagnitude = 0.35D;
        }

        double appliedHorizontal = horizontalMagnitude * horizontalScale;
        double appliedVertical = Math.max(0.0D, currentVelocity.getY()) * verticalScale;
        if (appliedVertical < 0.08D) {
            appliedVertical = 0.08D * verticalScale;
        }
        if (maxHorizontal > 0.0D) {
            appliedHorizontal = Math.min(appliedHorizontal, maxHorizontal);
        }
        if (maxVertical > 0.0D) {
            appliedVertical = Math.min(appliedVertical, maxVertical);
        }

        Vector adjustedVelocity = new Vector(
                awayFromAttacker.getX() * appliedHorizontal,
                appliedVertical,
                awayFromAttacker.getZ() * appliedHorizontal
        );

        target.setVelocity(adjustedVelocity);
    }

}
