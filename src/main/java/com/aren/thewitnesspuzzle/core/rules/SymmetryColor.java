package com.aren.thewitnesspuzzle.core.rules;

import com.aren.thewitnesspuzzle.core.color.ColorUtils;

public enum SymmetryColor {

    NONE, CYAN, YELLOW, CYAN2, YELLOW2;

    public int getRGB() {
        if (this == CYAN) {
            return ColorUtils.RGB(0, 255, 255);
        } else if (this == YELLOW) {
            return ColorUtils.RGB(255, 255, 0);
        } else if (this == CYAN2) {
            return ColorUtils.RGB("#60fccb");
        } else if (this == YELLOW2) {
            return ColorUtils.RGB("#f0d41d");
        }
        return 0;
    }

    public static SymmetryColor fromString(String str) {
        for (SymmetryColor color : SymmetryColor.values()) {
            if (color.toString().equalsIgnoreCase(str)) {
                return color;
            }
        }
        return null;
    }

    public boolean check(SymmetryColor other) {
        if (this == CYAN || this == CYAN2)
            return other == CYAN || other == CYAN2;
        if (this == YELLOW || this == YELLOW2)
            return other == YELLOW || other == YELLOW2;
        return true;
    }
}
