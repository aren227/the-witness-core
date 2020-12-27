package com.aren.thewitnesspuzzle.core.rules;

import com.aren.thewitnesspuzzle.core.color.ColorUtils;

public enum Color {
    BLACK, WHITE, ORANGE, LIME, PURPLE, CYAN, RED, YELLOW;

    public int getRGB() {
        if (this == BLACK) {
            return ColorUtils.RGB(0, 0, 0);
        } else if (this == WHITE) {
            return ColorUtils.RGB(255, 255, 255);
        } else if (this == ORANGE) {
            return ColorUtils.RGB(255, 128, 0);
        } else if (this == LIME) {
            return ColorUtils.RGB(79, 255, 79);
        } else if (this == PURPLE) {
            return ColorUtils.RGB(210, 0, 255);
        } else if (this == CYAN) {
            return ColorUtils.RGB(0, 255, 255);
        } else if (this == RED) {
            return ColorUtils.RGB(255, 65, 0);
        } else if (this == YELLOW) {
            return ColorUtils.RGB(255, 225, 0);
        }
        return 0;
    }

    public static Color fromString(String str) {
        for (Color color : Color.values()) {
            if (color.toString().equalsIgnoreCase(str)) {
                return color;
            }
        }
        return null;
    }
}