package com.aren.thewitnesspuzzle.core.color;

import org.json.JSONException;
import org.json.JSONObject;

public class PuzzleColorPalette {

    private int background;
    private int tile;
    private int path;
    private int cursor;
    private int cursorSucceeded;
    private int cursorFailed;

    private float bloomIntensity;

    public PuzzleColorPalette(int background, int path, int cursor) {
        this(background, path, cursor, cursor);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded) {
        this(background, path, cursor, cursorSucceeded, ColorUtils.RGB("#050a0f"));
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded, int cursorFailed) {
        this(background, path, cursor, cursorSucceeded, cursorFailed, 1f);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded, int cursorFailed, float bloomIntensity) {
        this(background, background, path, cursor, cursorSucceeded, cursorFailed, bloomIntensity);
    }

    public PuzzleColorPalette(int background, int tile, int path, int cursor, int cursorSucceeded, int cursorFailed, float bloomIntensity) {
        this.background = background;
        this.tile = tile;
        this.path = path;
        this.cursor = cursor;
        this.cursorSucceeded = cursorSucceeded;
        this.cursorFailed = cursorFailed;
        this.bloomIntensity = bloomIntensity;
    }

    public PuzzleColorPalette(JSONObject jsonObject) throws JSONException {
        background = jsonObject.getInt("background");
        if (jsonObject.has("tile"))
            tile = jsonObject.getInt("tile");
        else
            tile = background;
        path = jsonObject.getInt("path");
        cursor = jsonObject.getInt("cursor");
        cursorSucceeded = jsonObject.getInt("success");
        cursorFailed = jsonObject.getInt("failure");
        bloomIntensity = (float) jsonObject.getDouble("bloom");
    }

    @Override
    public PuzzleColorPalette clone() {
        return new PuzzleColorPalette(background, tile, path, cursor, cursorSucceeded, cursorFailed, bloomIntensity);
    }

    public int getBackgroundColor() {
        return background;
    }

    public void setBackgroundColor(int color) {
        background = color;
    }

    public int getTileColor() {
        return tile;
    }

    public void setTileColor(int color ){
        tile = color;
    }

    public int getPathColor() {
        return path;
    }

    public void setPathColor(int color) {
        path = color;
    }

    public int getCursorColor() {
        return cursor;
    }

    public void setCursorColor(int color) {
        cursor = color;
    }

    public int getCursorSucceededColor() {
        return cursorSucceeded;
    }

    public void setCursorSucceededColor(int color) {
        cursorSucceeded = color;
    }

    public int getCursorFailedColor() {
        return cursorFailed;
    }

    public void setCursorFailedColor(int color) {
        cursorFailed = color;
    }

    public float getBloomIntensity() {
        return bloomIntensity;
    }

    public void setBloomIntensity(float intensity) {
        bloomIntensity = intensity;
    }

    public void set(PuzzleColorPalette palette) {
        background = palette.getBackgroundColor();
        tile = palette.getTileColor();
        path = palette.getPathColor();
        cursor = palette.getCursorColor();
        cursorSucceeded = palette.getCursorSucceededColor();
        cursorFailed = palette.getCursorFailedColor();
    }

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("background", getBackgroundColor());
        jsonObject.put("tile", getTileColor());
        jsonObject.put("path", getPathColor());
        jsonObject.put("cursor", getCursorColor());
        jsonObject.put("success", getCursorSucceededColor());
        jsonObject.put("failure", getCursorFailedColor());
        jsonObject.put("bloom", (double) getBloomIntensity());
        return jsonObject;
    }
}
