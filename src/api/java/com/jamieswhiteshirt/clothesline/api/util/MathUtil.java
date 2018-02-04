package com.jamieswhiteshirt.clothesline.api.util;

public final class MathUtil {
    public static double floorMod(double x, double y) {
        double result = x % y;
        if (result >= 0.0) {
            return result;
        } else {
            return y + result;
        }
    }
}
