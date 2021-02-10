package com.aren.thewitnesspuzzle.core.puzzle;


import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.math.BoundingBox;
import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector2Int;
import com.aren.thewitnesspuzzle.core.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.core.rules.RemoveEdgeRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GridPuzzle extends PuzzleBase {

    public static final String NAME = "grid";

    protected int width, height;

    protected Vertex[][] gridVerticies;
    protected Edge[][] gridHorizontalEdges;
    protected Edge[][] gridVerticalEdges;
    protected Tile[][] gridTiles;

    public GridPuzzle(PuzzleColorPalette color, int width, int height) {
        super(color);

        this.width = width;
        this.height = height;

        gridVerticies = new Vertex[width + 1][height + 1];
        gridHorizontalEdges = new Edge[width][height + 1];
        gridVerticalEdges = new Edge[width + 1][height];
        gridTiles = new Tile[width][height];

        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                Vertex vertex = new Vertex(this, i, j);
                gridVerticies[i][j] = vertex;
            }
        }

        // Horizontal lines
        for (int i = 0; i < width; i++) {
            for (int j = 0; j <= height; j++) {
                Edge edge = new Edge(this, getVertexAt(i, j), getVertexAt(i + 1, j));
                gridHorizontalEdges[i][j] = edge;
            }
        }

        // Vertical lines
        for (int i = 0; i <= width; i++) {
            for (int j = 0; j < height; j++) {
                Edge edge = new Edge(this, getVertexAt(i, j), getVertexAt(i, j + 1));
                gridVerticalEdges[i][j] = edge;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Tile tile = new Tile(this, i + 0.5f, j + 0.5f);
                gridTiles[i][j] = tile;

                tile.edges.add(getEdgeAt(i, j, true));
                tile.edges.add(getEdgeAt(i, j, false));
                tile.edges.add(getEdgeAt(i, j + 1, true));
                tile.edges.add(getEdgeAt(i + 1, j, false));
            }
        }
    }

    public GridPuzzle(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");

        gridVerticies = new Vertex[width + 1][height + 1];
        gridHorizontalEdges = new Edge[width][height + 1];
        gridVerticalEdges = new Edge[width + 1][height];
        gridTiles = new Tile[width][height];

        for(Vertex vertex : vertices) {
            // Not aligned to grid
            if (vertex.getRule() instanceof EndingPointRule)
                continue;
            gridVerticies[vertex.getGridX()][vertex.getGridY()] = vertex;
        }

        for(Edge edge : edges) {
            // Not aligned to grid
            if (edge.from.getRule() instanceof EndingPointRule || edge.to.getRule() instanceof EndingPointRule)
                continue;

            if(edge.isHorizontal())
                gridHorizontalEdges[edge.getGridX()][edge.getGridY()] = edge;
            else
                gridVerticalEdges[edge.getGridX()][edge.getGridY()] = edge;
        }

        for(Tile tile : tiles) {
            int i = tile.getGridX();
            int j = tile.getGridY();
            gridTiles[i][j] = tile;

            tile.edges.add(getEdgeAt(i, j, true));
            tile.edges.add(getEdgeAt(i, j, false));
            tile.edges.add(getEdgeAt(i, j + 1, true));
            tile.edges.add(getEdgeAt(i + 1, j, false));
        }

        updateNotInAreaTiles();
    }

    @Override
    public float getPathWidth() {
        return (float) Math.log10(Math.max(width, height)) * 0.25f + 0.1f;
    }

    @Override
    public BoundingBox getBoundingBox() {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.addCircle(new Vector2(0, 0), 0.5f);
        boundingBox.addCircle(new Vector2(width, height), 0.5f);
        return boundingBox;
    }

    public Vertex getVertexAt(int x, int y) {
        return gridVerticies[x][y];
    }

    public Edge getEdgeAt(int x, int y, boolean horizontal) {
        if (horizontal) return gridHorizontalEdges[x][y];
        return gridVerticalEdges[x][y];
    }

    public Tile getTileAt(int x, int y) {
        return gridTiles[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addStartingPoint(int x, int y) {
        getVertexAt(x, y).setRule(new StartingPointRule());
    }

    public void removeStartingPoint(int x, int y) {
        getVertexAt(x, y).removeRule();
    }

    public Edge addEndingPoint(int x, int y) {
        if (isEndingPoint(x, y))
            return null;

        Vertex vertex = null;
        if (y == 0) {
            vertex = new Vertex(this, x, y - getPathWidth());
        } else if (y == height) {
            vertex = new Vertex(this, x, y + getPathWidth());
        } else if (x == 0) {
            vertex = new Vertex(this, x - getPathWidth(), y);
        } else if (x == width) {
            vertex = new Vertex(this, x + getPathWidth(), y);
        }

        if (vertex != null) {
            Edge edge = new Edge(this, getVertexAt(x, y), vertex);
            vertex.setRule(new EndingPointRule());
            return edge;
        }
        return null;
    }

    public void removeEndingPoint(int x, int y) {
        if (!isEndingPoint(x, y))
            return;

        Vertex vertex = getVertexAt(x, y);
        Vertex vertexToRemove = null;
        for (Vertex vertex1 : getConnectedVertices(vertex)) {
            if (vertex1.getRule() instanceof EndingPointRule) {
                vertexToRemove = vertex1;
                break;
            }
        }

        removeVertex(vertexToRemove);
    }

    public boolean isEndingPoint(int x, int y) {
        Vertex vertex = getVertexAt(x, y);
        for (Vertex vertex1 : getConnectedVertices(vertex)) {
            if (vertex1.getRule() instanceof EndingPointRule) {
                return true;
            }
        }
        return false;
    }

    /*@Override
    public ValidationResult validate(){
        GridAreaSplitter splitter = new GridAreaSplitter(cursor);
        ValidationResult result = new ValidationResult();
        for(Area area : splitter.areaList){
            result.areaValidationResults.add(area.validate(cursor));
        }

        //FIXME: Dirty code again. I think getVisitedVerticies() of SymmetryCursor should return with opposite vertices.
        List<Rule> rules = new ArrayList<>();
        for(Vertex vertex : cursor.getVisitedVertices()){
            if(vertex.getRule() != null) rules.add(vertex.getRule());
            if(this instanceof GridSymmetryPuzzle){
                Vertex opposite = ((GridSymmetryPuzzle)this).getOppositeVertex(vertex);
                if(opposite.getRule() != null) rules.add(opposite.getRule());
            }
        }
        for(Edge edge : cursor.getFullyVisitedEdges()){
            if(edge.getRule() != null) rules.add(edge.getRule());
            if(this instanceof GridSymmetryPuzzle){
                Edge opposite = ((GridSymmetryPuzzle)this).getOppositeEdge(edge);
                if(opposite.getRule() != null) rules.add(opposite.getRule());
            }
        }

        for(Rule rule : rules){
            if(!rule.validateLocally(cursor)){
                result.notOnAreaErrors.add(rule);
            }
        }

        return result;
    }*/

    public List<Vertex> getBorderVertices() {
        List<Vertex> vertices = new ArrayList<>();
        for (int x = 0; x <= width; x++) {
            vertices.add(getVertexAt(x, 0));
            vertices.add(getVertexAt(x, height));
        }
        for (int y = 1; y < height; y++) {
            vertices.add(getVertexAt(0, y));
            vertices.add(getVertexAt(width, y));
        }
        return vertices;
    }

    public List<Vertex> getInnerVertices() {
        List<Vertex> vertices = new ArrayList<>();
        for (int x = 1; x < width; x++) {
            for (int y = 1; y < height; y++) {
                vertices.add(getVertexAt(x, y));
            }
        }
        return vertices;
    }

    public void updateNotInAreaTiles() {
        boolean[][] notInArea = new boolean[width + 2][height + 2];

        Queue<Vector2Int> q = new LinkedList<>();
        q.offer(new Vector2Int(0, 0));
        while(q.size() > 0) {
            Vector2Int f = q.poll();

            if (notInArea[f.x][f.y])
                continue;
            notInArea[f.x][f.y] = true;

            if (f.x > 0 && (f.y == 0 || f.y == height + 1 || gridVerticalEdges[f.x - 1][f.y - 1].getRule() instanceof RemoveEdgeRule))
                q.offer(new Vector2Int(f.x - 1, f.y));
            if (f.x < width + 1 && (f.y == 0 || f.y == height + 1 || gridVerticalEdges[f.x][f.y - 1].getRule() instanceof RemoveEdgeRule))
                q.offer(new Vector2Int(f.x + 1, f.y));
            if (f.y > 0 && (f.x == 0 || f.x == width + 1 || gridHorizontalEdges[f.x - 1][f.y - 1].getRule() instanceof RemoveEdgeRule))
                q.offer(new Vector2Int(f.x, f.y - 1));
            if (f.y < height + 1 && (f.x == 0 || f.x == width + 1 || gridHorizontalEdges[f.x - 1][f.y].getRule() instanceof RemoveEdgeRule))
                q.offer(new Vector2Int(f.x, f.y + 1));
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                getTileAt(i, j).notInArea = notInArea[i + 1][j + 1];
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
        jsonObject.put("width", width);
        jsonObject.put("height", height);
    }

}
