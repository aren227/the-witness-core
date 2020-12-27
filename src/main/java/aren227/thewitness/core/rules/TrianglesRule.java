package aren227.thewitness.core.rules;

import aren227.thewitness.core.color.ColorUtils;
import aren227.thewitness.core.cursor.Cursor;
import aren227.thewitness.core.graph.Edge;
import aren227.thewitness.core.graph.Tile;
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
        if(eliminated) return true;

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
        jsonObject.put("count", count);
    }
}
