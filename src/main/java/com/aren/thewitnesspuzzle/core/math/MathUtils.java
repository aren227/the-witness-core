package com.aren.thewitnesspuzzle.core.math;

public class MathUtils {

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

}
