package com.aren.thewitnesspuzzle.core.rules;

public enum SymmetryType {

    VLINE, POINT;

    public static SymmetryType fromString(String str) {
        for (SymmetryType type : SymmetryType.values()) {
            if (type.toString().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }

}
