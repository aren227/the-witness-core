package com.aren.thewitnesspuzzle.core.rules;

import com.aren.thewitnesspuzzle.core.color.ColorUtils;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import org.json.JSONException;
import org.json.JSONObject;

public class TrianglesRule extends RuleBase {

    public static final String NAME = "triangles";

    public static final int COLOR = ColorUtils.RGB("#ffaa00");

    public int count;

    public TrianglesRule(int count) {
        super();
        this.count = count;
    }

    public TrianglesRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        count = jsonObject.getInt("count");
    }

    @Override
    public boolean validateLocally(Cursor cursor) {
        if (eliminated) return true;

        if (getGraphElement() instanceof Tile) {
            Tile tile = (Tile) getGraphElement();
            int c = 0;
            for (Edge edge : tile.edges) {
                if (cursor.containsEdge(edge)) c++;
            }
            return c == count;
        }
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
        jsonObject.put("count", count);
    }

    @Override
    public TrianglesRule clone() {
        try {
            return (TrianglesRule) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
