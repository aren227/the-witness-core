package aren227.thewitness.core.graph;

import aren227.thewitness.core.puzzle.PuzzleBase;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tile extends GraphElement {

    public List<Edge> edges = new ArrayList<>();

    public Tile(PuzzleBase puzzleBase, float x, float y) {
        super(puzzleBase, puzzleBase.getNextTileIndex(), x, y);
    }

    public Tile(PuzzleBase puzzleBase, JSONObject jsonObject) {
        super(puzzleBase, jsonObject);
    }

}
