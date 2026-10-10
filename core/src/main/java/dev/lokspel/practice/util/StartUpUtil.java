package dev.lokspel.practice.util;

import dev.lokspel.practice.AstralPractice;

public final class StartUpUtil {

    private StartUpUtil() {}

    public static void loadStartUpProgressMap() {
        for (StartUpTypes startUpType : StartUpTypes.values())
            AstralPractice.getInstance().getStartUpProgress().put(startUpType, false);
    }

    public static boolean isStartUpReady() {
        for (boolean b : AstralPractice.getInstance().getStartUpProgress().values())
            if (!b) return false;

        return true;
    }

}
