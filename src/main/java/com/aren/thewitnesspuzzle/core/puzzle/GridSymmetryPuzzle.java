package com.aren.thewitnesspuzzle.core.puzzle;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.core.rules.Symmetry;
import com.aren.thewitnesspuzzle.core.rules.SymmetryType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridSymmetryPuzzle extends GridPuzzle {

    public static final String NAME = "symmetry";

    protected Symmetry symmetry;

    protected Map<Integer, Vertex> oppositeVertex;
    protected Map<Integer, Edge> oppositeEdge;

    public GridSymmetryPuzzle(PuzzleColorPalette color, int width, int height, Symmetry symmetry) {
        super(color, width, height);

        this.symmetry = symmetry;

        calculateOppositeGraphElements();
    }

    public GridSymmetryPuzzle(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        this.symmetry = new Symmetry(jsonObject.getJSONObject("symmetry"));

        calculateOppositeGraphElements();
    }

    private void calculateOppositeGraphElements() {
        oppositeVertex = new HashMap<>();
        oppositeEdge = new HashMap<>();

        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                if (symmetry.getType() == SymmetryType.VLINE) {
                    oppositeVertex.put(getVertexAt(i, j).index, getVertexAt(width - i, j));
                } else if (symmetry.getType() == SymmetryType.POINT) {
                    oppositeVertex.put(getVertexAt(i, j).index, getVertexAt(width - i, height - j));
                }
            }
        }

        // Horizontal lines
        for (int i = 0; i < width; i++) {
            for (int j = 0; j <= height; j++) {
                if (symmetry.getType() == SymmetryType.VLINE) {
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, j, true));
                } else if (symmetry.getType() == SymmetryType.POINT) {
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, height - j, true));
                }
            }
        }

        // Vertical lines
        for (int i = 0; i <= width; i++) {
            for (int j = 0; j < height; j++) {
                if (symmetry.getType() == SymmetryType.VLINE) {
                    oppositeEdge.put(getEdgeAt(i, j, false).index, getEdgeAt(width - i, j, false));
                } else if (symmetry.getType() == SymmetryType.POINT) {
                    oppositeEdge.put(getEdgeAt(i, j, false).index, getEdgeAt(width - i, height - j - 1, false));
                }
            }
        }

        // Other vertices
        for (Vertex vertex : vertices) {
            if (!oppositeVertex.containsKey(vertex.index)) {
                Vertex op = null;
                if (symmetry.getType() == SymmetryType.VLINE) {
                    op = getVertexByPosition(width - vertex.x, vertex.y);
                } else if (symmetry.getType() == SymmetryType.POINT) {
                    op = getVertexByPosition(width - vertex.x, height - vertex.y);
                }

                if(op != null)
                    oppositeVertex.put(vertex.index, op);
                else
                    throw new RuntimeException("Invalid symmetry puzzle.");
            }
        }

        // Other lines
        for (Edge edge : edges) {
            if (!oppositeEdge.containsKey(edge.index)) {
                Vertex opFrom = getOppositeVertex(edge.from);
                Vertex opTo = getOppositeVertex(edge.to);

                Edge opEdge = getEdgeByVertex(opFrom, opTo);

                if (opEdge != null)
                    oppositeEdge.put(edge.index, opEdge);
                else
                    throw new RuntimeException("Invalid symmetry puzzle.");
            }
        }
    }

    public Symmetry getSymmetry() {
        return symmetry;
    }

    @Override
    public void addStartingPoint(int x, int y) {
        int ox, oy;
        if (symmetry.getType() == SymmetryType.VLINE) {
            ox = width - x;
            oy = y;
        } else {
            ox = width - x;
            oy = height - y;
        }

        if (x == ox && y == oy)
            return;

        super.addStartingPoint(x, y);
        super.addStartingPoint(ox, oy);
    }

    @Override
    public void removeStartingPoint(int x, int y) {
        super.removeStartingPoint(x, y);
        if (symmetry.getType() == SymmetryType.VLINE) super.removeStartingPoint(width - x, y);
        else if (symmetry.getType() == SymmetryType.POINT) super.removeStartingPoint(width - x, height - y);
    }

    @Override
    public Edge addEndingPoint(int x, int y) {
        if (isEndingPoint(x, y))
            return null;

        int ox, oy;
        if (symmetry.getType() == SymmetryType.VLINE) {
            ox = width - x;
            oy = y;
        } else {
            ox = width - x;
            oy = height - y;
        }

        if (x == ox && y == oy)
            return null;

        Edge edge1 = super.addEndingPoint(x, y);
        Edge edge2 = super.addEndingPoint(ox, oy);

        // edge1 and edge2 are already opposite each other (check super.addEndingPoint)
        if (edge2 != null) {
            oppositeVertex.put(edge1.to.index, edge2.to);
            oppositeVertex.put(edge2.to.index, edge1.to);

            oppositeEdge.put(edge1.index, edge2);
            oppositeEdge.put(edge2.index, edge1);
        }
        return edge1;
    }

    @Override
    public void removeVertex(Vertex vertex) {
        if (!vertices.contains(vertex))
            return;

        Vertex oppoVertex = getOppositeVertex(vertex);

        vertices.remove(vertex);
        vertices.remove(oppoVertex);

        oppositeVertex.remove(vertex.index);
        oppositeVertex.remove(oppoVertex.index);

        List<Edge> edgesToRemove = new ArrayList<>();
        for (Edge edge : getEdges()) {
            if (edge.from == vertex || edge.to == vertex || edge.from == oppoVertex || edge.to == oppoVertex) {
                edgesToRemove.add(edge);
            }
        }

        for (Edge edge : edgesToRemove) {
            if (edge.from == vertex) {
                edge.to.adj.remove(vertex);
            }
            if (edge.to == vertex) {
                edge.from.adj.remove(vertex);
            }
            if (edge.from == oppoVertex) {
                edge.to.adj.remove(oppoVertex);
            }
            if (edge.to == oppoVertex) {
                edge.from.adj.remove(oppoVertex);
            }

            for (Tile tile : getTiles()) {
                tile.edges.remove(edge);
            }

            oppositeEdge.remove(edge.index);
        }

        edges.removeAll(edgesToRemove);
    }

    public Vertex getOppositeVertex(Vertex vertex) {
        if (oppositeVertex.containsKey(vertex.index)) return oppositeVertex.get(vertex.index);
        return null;
    }

    public Edge getOppositeEdge(Edge edge) {
        if (oppositeEdge.containsKey(edge.index)) {
            return oppositeEdge.get(edge.index);
        }
        return null;
    }

    public EdgeProportion getOppositeEdgeProportion(EdgeProportion edgeProportion) {
        EdgeProportion opposite = new EdgeProportion(getOppositeEdge(edgeProportion.edge));
        opposite.proportion = edgeProportion.proportion;
        opposite.reverse = edgeProportion.reverse;
        if (symmetry.getType() == SymmetryType.VLINE) {
            if (opposite.edge.isHorizontal()) opposite.reverse = !opposite.reverse;
            return opposite;
        } else if (symmetry.getType() == SymmetryType.POINT) {
            // If it contains ending point, the direction of two opposite edges are already symmetric
            if (!(edgeProportion.to().getRule() instanceof EndingPointRule)) {
                opposite.reverse = !opposite.reverse;
            }
            return opposite;
        }
        return null;
    }

    @Override
    public Cursor createCursor(Vertex start) {
        return new SymmetryCursor(this, start);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
        jsonObject.put("symmetry", symmetry.serialize());
    }
}
