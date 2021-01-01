package com.aren.thewitnesspuzzle.core.rules;

import org.json.JSONObject;

public class EndingPointRule extends RuleBase {

    public static final String NAME = "end";

    public EndingPointRule() {
        super();
    }

    public EndingPointRule(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public EndingPointRule clone() {
        try {
            return (EndingPointRule) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
