package com.aren.thewitnesspuzzle.core.rules;

import com.aren.thewitnesspuzzle.core.color.ColorUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class EliminationRule extends Colorable {

    public static final String NAME = "elimination";

    public static final int COLOR = ColorUtils.RGB("#fafafa");

    public EliminationRule(){
        this(Color.WHITE);
    }

    public EliminationRule(Color color) {
        super(color);
    }

    public EliminationRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    public boolean canValidateLocally() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
