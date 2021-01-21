package com.aren.thewitnesspuzzle.core.puzzle;

import com.aren.thewitnesspuzzle.core.color.ColorUtils;
import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import org.json.JSONObject;

public class ErrorPuzzle extends PuzzleBase {

    public static final String NAME = "error";

    public ErrorPuzzle() {
        super(new PuzzleColorPalette(ColorUtils.RGB(255, 0, 0), ColorUtils.RGB(0, 0, 0),
                ColorUtils.RGB(255, 255, 255), ColorUtils.RGB(255, 255, 255), ColorUtils.RGB(255, 255, 255), 0));

        Vertex center = new Vertex(this, 0, 0);
        Vertex ul = new Vertex(this,  -1, 1);
        Vertex ur = new Vertex(this, 1, 1);
        Vertex ll = new Vertex(this, -1, -1);
        Vertex lr = new Vertex(this, 1, -1);
        Vertex ul_ = new Vertex(this,  -1.1f, 1.1f);
        Vertex ur_ = new Vertex(this, 1.1f, 1.1f);
        Vertex ll_ = new Vertex(this, -1.1f, -1.1f);
        Vertex lr_ = new Vertex(this, 1.1f, -1.1f);

        new Edge(this, center, ul);
        new Edge(this, center, ur);
        new Edge(this, center, ll);
        new Edge(this, center, lr);
        new Edge(this, ul, ul_);
        new Edge(this, ur, ur_);
        new Edge(this, ll, ll_);
        new Edge(this, lr, lr_);

        center.setRule(new StartingPointRule());
        ul_.setRule(new EndingPointRule());
        ur_.setRule(new EndingPointRule());
        ll_.setRule(new EndingPointRule());
        ll_.setRule(new EndingPointRule());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void serialize(JSONObject jsonObject) {
        throw new UnsupportedOperationException();
    }
}
