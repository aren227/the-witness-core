package com.aren.thewitnesspuzzle.core.rules;

import com.aren.thewitnesspuzzle.core.cursor.area.Area;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SquareRule extends Colorable {

    public static final String NAME = "square";

    public SquareRule(Color color) {
        super(color);
    }

    public SquareRule(JSONObject jsonObject) throws JSONException {
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

    public static List<RuleBase> areaValidate(Area area) {
        Map<Color, ArrayList<RuleBase>> squareColors = new HashMap<>();
        for (Tile tile : area.tiles) {
            if (tile.getRule() instanceof SquareRule) {
                SquareRule square = (SquareRule) tile.getRule();
                if (square.eliminated) continue;
                if (!squareColors.containsKey(square.color))
                    squareColors.put(square.color, new ArrayList<>());
                squareColors.get(square.color).add(square);
            }
        }

        if (squareColors.keySet().size() <= 1) return new ArrayList<>();

        List<Integer> sizes = new ArrayList<>();
        for (Color color : squareColors.keySet()) {
            sizes.add(squareColors.get(color).size());
        }
        Collections.sort(sizes);
        int errorMaxSize = sizes.get(sizes.size() - 2);

        List<RuleBase> areaErrors = new ArrayList<>();
        for (Color color : squareColors.keySet()) {
            if (squareColors.get(color).size() <= errorMaxSize) {
                areaErrors.addAll(squareColors.get(color));
            }
        }
        return areaErrors;
    }

    @Override
    public SquareRule clone() {
        try {
            return (SquareRule) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
