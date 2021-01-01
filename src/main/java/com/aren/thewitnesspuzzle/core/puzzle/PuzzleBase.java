package com.aren.thewitnesspuzzle.core.puzzle;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.GraphElement;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.math.BoundingBox;
import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.rules.RuleBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PuzzleBase {

    public static final String NAME = "general";

    protected ArrayList<Vertex> vertices = new ArrayList<>();
    protected ArrayList<Edge> edges = new ArrayList<>();
    //protected Edge[][] edgeTable; // Indexed by two vertices pair
    protected ArrayList<Tile> tiles = new ArrayList<>();

    protected PuzzleColorPalette color;

    protected BoundingBox boundingBox = new BoundingBox();

    // Manipulate path width (not serialized)
    protected float overridePathWidth = 0;

    public PuzzleBase(PuzzleColorPalette color){
        this.color = color;
    }

    public PuzzleBase(JSONObject jsonObject) {
        try {
            this.color = new PuzzleColorPalette(jsonObject.getJSONObject("color"));

            JSONArray vertexArray = jsonObject.getJSONArray("vertices");
            for(int i = 0; i < vertexArray.length(); i++)
                new Vertex(this, vertexArray.getJSONObject(i));

            JSONArray edgeArray = jsonObject.getJSONArray("edges");
            for(int i = 0; i < edgeArray.length(); i++)
                new Edge(this, edgeArray.getJSONObject(i));

            JSONArray tileArray = jsonObject.getJSONArray("tiles");
            for(int i = 0; i < tileArray.length(); i++)
                new Tile(this, tileArray.getJSONObject(i));

            for(Vertex vertex : vertices)
                boundingBox.addCircle(vertex.getPosition(), 0.5f);

        } catch (JSONException ignored) {

        }
    }

    public void setColorPalette(PuzzleColorPalette color) {
        this.color = color;
    }

    public PuzzleColorPalette getColorPalette() {
        return color;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public float getPathWidth() {
        if(overridePathWidth > 0) return overridePathWidth;
        return Math.max(getBoundingBox().getWidth(), getBoundingBox().getHeight()) * 0.05f + 0.05f;
    }

    public void setOverridePathWidth(float pathWidth){
        overridePathWidth = pathWidth;
    }

    public Vertex getVertex(int index) {
        for (Vertex vertex : vertices) {
            if (vertex.index == index) return vertex;
        }
        return null;
    }

    public List<Vertex> getConnectedVertices(Vertex vertex) {
        List<Vertex> result = new ArrayList<>();
        for (Edge edge : getEdges()) {
            if (edge.from == vertex) {
                result.add(edge.to);
            } else if (edge.to == vertex) {
                result.add(edge.from);
            }
        }
        return result;
    }

    public Edge addEdge(int va, int vb){
        return new Edge(this, getVertex(va), getVertex(vb));
    }

    public void removeVertex(Vertex vertex) {
        if (!vertices.contains(vertex))
            return;

        vertices.remove(vertex);

        List<Edge> edgesToRemove = new ArrayList<>();
        for (Edge edge : getEdges()) {
            if (edge.from == vertex || edge.to == vertex) {
                edgesToRemove.add(edge);
            }
        }

        for (Edge edge : edgesToRemove) {
            if (edge.from == vertex) {
                edge.to.adj.remove(vertex);
            } else if (edge.to == vertex) {
                edge.from.adj.remove(vertex);
            }

            for (Tile tile : getTiles()) {
                tile.edges.remove(edge);
            }
        }

        edges.removeAll(edgesToRemove);
    }

    public void register(GraphElement graphElement) {
        if(graphElement instanceof Vertex)
            vertices.add((Vertex) graphElement);
        else if(graphElement instanceof Edge) {
            edges.add((Edge) graphElement);
        }
        else if(graphElement instanceof Tile)
            tiles.add((Tile) graphElement);

        boundingBox.addCircle(graphElement.getPosition(), 0.5f);
    }

    public Edge getNearestEdge(Vector2 pos) {
        float minDist = Float.MAX_VALUE;
        Edge minEdge = null;
        for (Edge edge : edges) {
            float dist = edge.getDistance(pos);
            if (dist < minDist) {
                minDist = dist;
                minEdge = edge;
            }
        }
        return minEdge;
    }

    // Calculate distance using the center of the element
    public GraphElement getNearestGraphElement(Vector2 pos, boolean containsVertices, boolean containsEdges, boolean containsTiles) {
        float minDist = Float.MAX_VALUE;
        GraphElement result = null;
        List<GraphElement> graphElements = new ArrayList<>();
        if (containsVertices) graphElements.addAll(getVertices());
        if (containsEdges) graphElements.addAll(getEdges());
        if (containsTiles) graphElements.addAll(getTiles());

        for (GraphElement graphElement : graphElements) {
            if (pos.distance(graphElement.getPosition()) < minDist) {
                minDist = pos.distance(graphElement.getPosition());
                result = graphElement;
            }
        }

        return result;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public int getNextVertexIndex() {
        int maxIdx = 0;
        for (Vertex vertex : getVertices()) {
            maxIdx = Math.max(maxIdx, vertex.index);
        }
        return maxIdx + 1;
    }

    public int getNextEdgeIndex() {
        int maxIdx = 0;
        for (Edge edge : getEdges()) {
            maxIdx = Math.max(maxIdx, edge.index);
        }
        return maxIdx + 1;
    }

    public int getNextTileIndex() {
        int maxIdx = 0;
        for (Tile tile : getTiles()) {
            maxIdx = Math.max(maxIdx, tile.index);
        }
        return maxIdx + 1;
    }

    public Vertex getVertexByPosition(float x, float y) {
        Vector2 targetPosition = new Vector2(x, y);
        for (Vertex v : vertices) {
            if (v.getPosition().distance(targetPosition) < 1e-5)
                return v;
        }
        return null;
    }

    /* private void calcEdgeTable() {
        edgeTable = new Edge[getVertices().size()][getVertices().size()];
        for (Edge edge : getEdges()) {
            edgeTable[edge.from.index][edge.to.index] = edge;
            edgeTable[edge.to.index][edge.from.index] = edge;
        }
    } */

    public Edge getEdgeByVertex(Vertex from, Vertex to) {
        /* if (edgeTable == null) calcEdgeTable();
        return edgeTable[from.index][to.index]; */
        for (Edge edge : getEdges()) {
            if (edge.from == from && edge.to == to)
                return edge;
        }
        return null;
    }

    public List<RuleBase> getAllRules() {
        List<RuleBase> rules = new ArrayList<>();
        for (Vertex vertex : vertices) {
            if (vertex.getRule() != null) {
                rules.add(vertex.getRule());
            }
        }
        for (Edge edge : edges) {
            if (edge.getRule() != null) {
                rules.add(edge.getRule());
            }
        }
        for (Tile tile : tiles) {
            if (tile.getRule() != null) {
                rules.add(tile.getRule());
            }
        }
        return rules;
    }

    public Cursor createCursor(Vertex start) {
        return new Cursor(this, start);
    }

    public String getName() {
        return NAME;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        serialize(jsonObject);
        return jsonObject;
    }

    protected void serialize(JSONObject jsonObject) throws JSONException {
        JSONArray vertexArray = new JSONArray();
        for(Vertex vertex : vertices)
            vertexArray.put(vertex.serialize());
        jsonObject.put("vertices", vertexArray);

        JSONArray edgeArray = new JSONArray();
        for(Edge edge : edges)
            edgeArray.put(edge.serialize());
        jsonObject.put("edges", edgeArray);

        JSONArray tileArray = new JSONArray();
        for(Tile tile : tiles)
            tileArray.put(tile.serialize());
        jsonObject.put("tiles", tileArray);

        jsonObject.put("color", color.serialize());

        jsonObject.put("type", getName());
    }

    public static PuzzleBase deserialize(JSONObject jsonObject) throws JSONException {
        String type = jsonObject.getString("type");
        switch (type) {
            case PuzzleBase.NAME:
                return new PuzzleBase(jsonObject);
            case GridPuzzle.NAME:
                return new GridPuzzle(jsonObject);
            case GridSymmetryPuzzle.NAME:
                return new GridSymmetryPuzzle(jsonObject);
            case HexagonPuzzle.NAME:
                return new HexagonPuzzle(jsonObject);
            case JunglePuzzle.NAME:
                return new JunglePuzzle(jsonObject);
            case VideoRoomPuzzle.NAME:
                return new VideoRoomPuzzle(jsonObject);
        }
        return null;
    }
}
