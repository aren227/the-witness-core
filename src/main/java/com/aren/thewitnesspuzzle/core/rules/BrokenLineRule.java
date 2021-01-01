package com.aren.thewitnesspuzzle.core.rules;

import org.json.JSONException;
import org.json.JSONObject;

public class BrokenLineRule extends RuleBase {

    public static final String NAME = "brokenline";

    // Manipulate width
    private float overrideCollisionCircleRadius = 0f;

    public BrokenLineRule() {
        super();
    }

    public BrokenLineRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        overrideCollisionCircleRadius = (float) jsonObject.getDouble("collRadius");
    }

    @Override
    public String getName() {
        return NAME;
    }

    public float getCollisionCircleRadius() {
        if(overrideCollisionCircleRadius > 0) return overrideCollisionCircleRadius;
        return 0.07f / getGraphElement().getPuzzleBase().getPathWidth() * 0.5f;
    }

    public void setOverrideCollisionCircleRadius(float collisionCircleRadius){
        overrideCollisionCircleRadius = collisionCircleRadius;
    }

    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);

        jsonObject.put("collRadius", overrideCollisionCircleRadius);
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
