package com.aren.thewitnesspuzzle.core.puzzle;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoRoomPuzzle extends PuzzleBase {

    public static final String NAME = "video-room";

    public VideoRoomPuzzle(PuzzleColorPalette color) {
        super(color);

        List<Vertex> list = new ArrayList<>();

        float h = (float)(Math.sqrt(3) / 2);
        float el = 0.3f;

        // 0
        list.add(new Vertex(this, -2.5f, -h));
        list.add(new Vertex(this, -2.5f, h));

        // 2
        list.add(new Vertex(this, -2f, -h * 2));
        list.add(new Vertex(this, -2f, 0));
        list.add(new Vertex(this, -2f, h * 2));

        // 5
        list.add(new Vertex(this, -1f, -h * 2));
        list.add(new Vertex(this, -1f, 0));
        list.add(new Vertex(this, -1f, h * 2));

        // 8
        list.add(new Vertex(this, -0.5f, -h * 3));
        list.add(new Vertex(this, -0.5f, -h * 1));
        list.add(new Vertex(this, -0.5f, h * 1));
        list.add(new Vertex(this, -0.5f, h * 3));

        // 12
        list.add(new Vertex(this, 0.5f, -h * 3));
        list.add(new Vertex(this, 0.5f, -h * 1));
        list.add(new Vertex(this, 0.5f, h * 1));
        list.add(new Vertex(this, 0.5f, h * 3));

        // 16
        list.add(new Vertex(this, 1f, -h * 2));
        list.add(new Vertex(this, 1f, 0));
        list.add(new Vertex(this, 1f, h * 2));

        // 19
        list.add(new Vertex(this, 2f, -h * 2));
        list.add(new Vertex(this, 2f, 0));
        list.add(new Vertex(this, 2f, h * 2));

        // 22
        list.add(new Vertex(this, 2.5f, -h));
        list.add(new Vertex(this, 2.5f, h));

        // 24
        list.add(new Vertex(this, -2f - el * 0.5f, -2 * h - el * h));
        list.get(24).setRule(new EndingPointRule());

        // 25
        list.add(new Vertex(this, -1.5f, h * 2));
        list.get(25).setRule(new StartingPointRule());

        // 26
        list.add(new Vertex(this, -0.5f - el * 0.5f, h * 3 + el * h));
        list.get(26).setRule(new EndingPointRule());

        // 27
        list.add(new Vertex(this, 0, -h));
        list.get(27).setRule(new StartingPointRule());

        // 28
        list.add(new Vertex(this, 0.5f - el * 0.5f, h - el * h));
        list.get(28).setRule(new EndingPointRule());

        // 29
        list.add(new Vertex(this, 1.5f, -2 * h));
        list.add(new Vertex(this, 1.5f, -2 * h - el));
        list.get(30).setRule(new EndingPointRule());

        // 31
        list.add(new Vertex(this, 1.5f, 0));
        list.add(new Vertex(this, 1.5f, el));
        list.get(32).setRule(new EndingPointRule());

        // 33
        list.add(new Vertex(this, 2.5f + el, -h));
        list.get(33).setRule(new EndingPointRule());

        new Edge(this, list.get(24), list.get(2));
        new Edge(this, list.get(2), list.get(0));
        new Edge(this, list.get(0), list.get(3));
        new Edge(this, list.get(3), list.get(1));
        new Edge(this, list.get(1), list.get(4));

        new Edge(this, list.get(2), list.get(5));
        new Edge(this, list.get(3), list.get(6));
        new Edge(this, list.get(4), list.get(25));
        new Edge(this, list.get(25), list.get(7));

        new Edge(this, list.get(8), list.get(5));
        new Edge(this, list.get(5), list.get(9));
        new Edge(this, list.get(9), list.get(6));
        new Edge(this, list.get(6), list.get(10));
        new Edge(this, list.get(10), list.get(7));
        new Edge(this, list.get(7), list.get(11));
        new Edge(this, list.get(11), list.get(26));

        new Edge(this, list.get(8), list.get(12));
        new Edge(this, list.get(9), list.get(27));
        new Edge(this, list.get(27), list.get(13));
        new Edge(this, list.get(10), list.get(14));
        new Edge(this, list.get(11), list.get(15));

        new Edge(this, list.get(12), list.get(16));
        new Edge(this, list.get(16), list.get(13));
        new Edge(this, list.get(13), list.get(17));
        new Edge(this, list.get(17), list.get(14));
        new Edge(this, list.get(14), list.get(28));
        new Edge(this, list.get(14), list.get(18));
        new Edge(this, list.get(18), list.get(15));

        new Edge(this, list.get(16), list.get(29));
        new Edge(this, list.get(29), list.get(30));
        new Edge(this, list.get(29), list.get(19));
        new Edge(this, list.get(17), list.get(31));
        new Edge(this, list.get(31), list.get(32));
        new Edge(this, list.get(31), list.get(20));
        new Edge(this, list.get(18), list.get(21));

        new Edge(this, list.get(19), list.get(22));
        new Edge(this, list.get(22), list.get(33));
        new Edge(this, list.get(22), list.get(20));
        new Edge(this, list.get(20), list.get(23));
        new Edge(this, list.get(23), list.get(21));
    }

    public VideoRoomPuzzle(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public float getPathWidth() {
        return 0.26f;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
    }
}
