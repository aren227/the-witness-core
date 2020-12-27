package com.aren.thewitnesspuzzle.core.puzzle;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import org.json.JSONObject;

public class ErrorPuzzle extends PuzzleBase {

    public static final String NAME = "error";

    public ErrorPuzzle() {
        super(new PuzzleColorPalette(0, 0, 0, 0, 0, 0));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void serialize(JSONObject jsonObject) {
        throw new UnsupportedOperationException();
    }
}
