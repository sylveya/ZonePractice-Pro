package dev.lokspel.practice.util;

import org.bukkit.Bukkit;

public final class TPSUtil {

    private static final double MAX_TPS = 20.0;
    private static final int SCALE = 100;

    private TPSUtil() {}

    public static double get1MinTPS() {
        return round(Bukkit.getTPS()[0]);
    }

    private static double round(double value) {
        return Math.min(MAX_TPS, Math.round(value * SCALE) / (double) SCALE);
    }

}