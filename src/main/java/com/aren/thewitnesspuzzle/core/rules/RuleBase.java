package com.aren.thewitnesspuzzle.core.rules;

import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.GraphElement;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RuleBase implements Cloneable {

    private GraphElement graphElement;

    public boolean eliminated;

    public RuleBase() {

    }

    public RuleBase(JSONObject jsonObject) {

    }

    public GraphElement getGraphElement() {
        return graphElement;
    }

    public void setGraphElement(GraphElement graphElement) {
        this.graphElement = graphElement;
    }

    public boolean validateLocally(Cursor cursor) {
        return true;
    }

    public boolean canValidateLocally() {
        return true;
    }

    public abstract String getName();

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        serialize(jsonObject);
        return jsonObject;
    }

    protected void serialize(JSONObject jsonObject) throws JSONException {
        jsonObject.put("type", getName());
    }

    public static RuleBase deserialize(JSONObject jsonObject) throws JSONException {
        String type = jsonObject.getString("type");
        switch (type) {
            case BlocksRule.NAME:
                return new BlocksRule(jsonObject);
            case BrokenLineRule.NAME:
                return new BrokenLineRule(jsonObject);
            case EliminationRule.NAME:
                return new EliminationRule(jsonObject);
            case EndingPointRule.NAME:
                return new EndingPointRule(jsonObject);
            case HexagonRule.NAME:
                return new HexagonRule(jsonObject);
            case RemoveEdgeRule.NAME:
                return new RemoveEdgeRule(jsonObject);
            case SquareRule.NAME:
                return new SquareRule(jsonObject);
            case StartingPointRule.NAME:
                return new StartingPointRule(jsonObject);
            case SunRule.NAME:
                return new SunRule(jsonObject);
            case TrianglesRule.NAME:
                return new TrianglesRule(jsonObject);
        }
        return null;
    }

}
