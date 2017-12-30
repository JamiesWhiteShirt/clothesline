package com.jamieswhiteshirt.clothesline.common;

public class Util {
    /**
     * Forge really likes annotation magic. This makes static analysis tools shut up.
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> T nonNullInjected() {
        return null;
    }
}
