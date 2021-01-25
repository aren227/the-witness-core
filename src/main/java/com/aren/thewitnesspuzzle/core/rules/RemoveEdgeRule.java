package com.aren.thewitnesspuzzle.core.rules;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoveEdgeRule extends RuleBase {

    public static final String NAME = "remove_edge";

    public RemoveEdgeRule() {
        super();
    }

    public RemoveEdgeRule(JSONObject jsonObject) throws JSONException {
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

    @Override
    public RemoveEdgeRule clone() {
        try {
            return (RemoveEdgeRule) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
