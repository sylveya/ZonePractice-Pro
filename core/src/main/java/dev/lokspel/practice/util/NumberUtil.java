package dev.lokspel.practice.util;

import java.text.DecimalFormat;

public final class NumberUtil {

    private NumberUtil() {}

    public static double roundDouble(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.parseDouble(df.format(value).replace(",", "."));
    }

    public static int doubleToInt(double value) {
        return (int) value;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static boolean isNotInteger(String s) {
        return !isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

}
