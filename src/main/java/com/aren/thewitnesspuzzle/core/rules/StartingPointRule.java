package com.aren.thewitnesspuzzle.core.rules;

import org.json.JSONObject;

public class StartingPointRule extends RuleBase {

    public static final String NAME = "start";

    public StartingPointRule() {
        super();
    }

    public StartingPointRule(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public float getRadius() {
        return getGraphElement().getPuzzleBase().getPathWidth() * 1.1f;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
