package com.aren.thewitnesspuzzle.core.rules;

import org.json.JSONObject;

// Actually, it's not a rule but for convenience.
public class SquareVertexRule extends RuleBase {

    public static final String NAME = "squarevertex";

    public SquareVertexRule() {
        super();
    }

    public SquareVertexRule(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public SquareVertexRule clone() {
        try {
            return (SquareVertexRule) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

}
