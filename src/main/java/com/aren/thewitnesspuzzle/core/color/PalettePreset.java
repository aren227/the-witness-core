package com.aren.thewitnesspuzzle.core.color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalettePreset {

    public static PalettePreset instance = new PalettePreset();

    private Map<String, PuzzleColorPalette> presets = new HashMap<>();

    public PalettePreset() {
        register("General_Slide", "#fd9700", "#8a4700", "#fffef9", "#fbe50d", "#050a0f", 0.5f);
        register("General_Panel", "#c9c2b9", "#665031", "#e6a35e", "#f77b20", "#f52c27", 0.1f);
        register("Entry_1", "#e6a500", "#483501", "#f6f39f", "#fedf50", "#050a0f", 0.5f);
        register("Green_1", "#0ee851", "#006615", "#eaeabc", "#b8ff14", "#050a0f", 0.5f);
        register("Blue_1", "#514eed", "#1405a1", "#aaccf4", "#659cff", "#050a0f", 0.5f);
        register("GlassFactory_1", "#436691", "#24385e", "#e4e6e1", "#18fe9d", "#050a0f", 0.2f);
        register("SymmetryIsland_1", "#96c2e9", "#555553", "#fafafa", "#fafafa", "#fe0d16", 0.2f); // cursor color is cyan & yellow
        register("Swamp_1", "#787d1a", "#9fad17", "#dbd670", "#f5f6ab", "#fcf31a", "#050a0f", 0.0f);
        register("Swamp_2", "#a67928", "#c28b25", "#e6c97e", "#f4e791", "#f2f528", "#050a0f", 0.0f);
        register("Swamp_3", "#6e2624", "#992c29", "#e35746", "#f1f2d2", "#fdf61a", "#050a0f", 0.0f);
        register("Swamp_4", "#0f0f0f", "#767676", "#4c4c4c", "#d7b462", "#122bdf", "#050a0f", 0.0f);
        register("Jungle", "#d66060", "#706653", "#ffffff", "#fed073", "#62428a", 0.5f);
        register("Treehouse_1", "#505b5f", "#0f1412", "#fef7e1", "#fda20f", "#050a0f", 0.0f);
        register("Treehouse_2", "#505b5f", "#0f1412", "#fef7e1", "#ac01be", "#050a0f", 0.0f);
        register("Treehouse_3", "#505b5f", "#0f1412", "#fef7e1", "#00fa00", "#050a0f", 0.0f);
        register("Quarry_1", "#363838", "#59877c", "#ffffff", "#ffffff", "#050a0f", 1.0f);
        register("Challenge_1", "#40c985", "#535c53", "#fad8ca", "#ffd49c", "#050a0f", 0.2f);
        register("Challenge_2", "#000000", "#79600e", "#ffd585", "#fe9f00", "#050a0f", 1.0f);
        register("Challenge_3", "#0ec29b", "#304352", "#fafafa", "#fafafa", "#050a0f", 1.0f);
        register("Challenge_4", "#26230e", "#947328", "#fafafa", "#fafafa", "#050a0f", 1.0f);
        register("Desert_1", "#3c0300", "#000000", "#fff0b3", "#ff8400", "#050a0f", 1.0f);
        register("Desert_2", "#1e3d61", "#04114d", "#0268ac", "#0024d4", "#050a0f", 1.0f);
    }

    private void register(String name, String background, String path, String cursor, String cursorSucceeded, String cursorFailed, float bloomIntensity) {
        presets.put(name, new PuzzleColorPalette(ColorUtils.RGB(background), ColorUtils.RGB(path), ColorUtils.RGB(cursor), ColorUtils.RGB(cursorSucceeded), ColorUtils.RGB(cursorFailed), bloomIntensity));
    }

    private void register(String name, String background, String tile, String path, String cursor, String cursorSucceeded, String cursorFailed, float bloomIntensity) {
        presets.put(name, new PuzzleColorPalette(ColorUtils.RGB(background), ColorUtils.RGB(tile), ColorUtils.RGB(path), ColorUtils.RGB(cursor), ColorUtils.RGB(cursorSucceeded), ColorUtils.RGB(cursorFailed), bloomIntensity));
    }

    public static PuzzleColorPalette get(String name) {
        if (instance.presets.containsKey(name)) return instance.presets.get(name).clone();
        return null;
    }

    public static List<PuzzleColorPalette> getAll() {
        return new ArrayList<>(instance.presets.values());
    }

}
