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

    public SymmetryColor getOppositeColor() {
        switch (this) {
            case CYAN:
                return YELLOW;
            case YELLOW:
                return CYAN;
            case CYAN2:
                return YELLOW2;
            case YELLOW2:
                return CYAN2;
        }
        return NONE;
    }

    public SymmetryColor changeColorTheme(SymmetryColor colorTheme) {
        if (this == NONE || colorTheme == NONE || colorTheme == null)
            return this;
        if (colorTheme == SymmetryColor.CYAN || colorTheme == SymmetryColor.YELLOW) {
            if (this == CYAN2)
                return CYAN;
            if (this == YELLOW2)
                return YELLOW;
            return this;
        } else {
            if (this == CYAN)
                return CYAN2;
            if (this == YELLOW)
                return YELLOW2;
            return this;
        }
    }
}
