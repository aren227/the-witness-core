package com.aren.thewitnesspuzzle.core.puzzle;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import org.json.JSONException;
import org.json.JSONObject;

public class HexagonPuzzle extends PuzzleBase {

    public static final String NAME = "hexagon";

    public HexagonPuzzle(PuzzleColorPalette color) {
        super(color);

        Vertex center = new Vertex(this, 0, 0);
        Vertex[] innerVertices = new Vertex[6];
        Vertex[] outerVertices = new Vertex[6];
        for (int i = 0; i < 6; i++) {
            innerVertices[i] = new Vertex(this, -(float) Math.sin(i / 6f * Math.PI * 2) * 6f, (float) Math.cos(i / 6f * Math.PI * 2) * 6f);
            outerVertices[i] = new Vertex(this, -(float) Math.sin(i / 6f * Math.PI * 2) * 7.5f, (float) Math.cos(i / 6f * Math.PI * 2) * 7.5f);
        }

        for (int i = 0; i < 6; i++) {
            new Edge(this, center, innerVertices[i]);
        }

        for (int i = 0; i < 6; i++) {
            new Edge(this, innerVertices[i], innerVertices[(i + 1) % 6]);
        }

        for (int i = 0; i < 6; i++) {
            new Edge(this, innerVertices[i], outerVertices[i]);
        }

        center.setRule(new StartingPointRule());
        for (int i = 0; i < 6; i++) {
            innerVertices[i].setRule(new StartingPointRule());
        }
        for (int i = 0; i < 6; i++) {
            outerVertices[i].setRule(new EndingPointRule());
        }
    }

    public HexagonPuzzle(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
    }
}
