package aren227.thewitness.core.puzzle;

import aren227.thewitness.core.color.PuzzleColorPalette;
import aren227.thewitness.core.cursor.Cursor;
import aren227.thewitness.core.cursor.SymmetryCursor;
import aren227.thewitness.core.graph.Edge;
import aren227.thewitness.core.graph.EdgeProportion;
import aren227.thewitness.core.graph.Vertex;
import aren227.thewitness.core.rules.EndingPointRule;
import aren227.thewitness.core.rules.Symmetry;
import aren227.thewitness.core.rules.SymmetryType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
        super.addStartingPoint(x, y);
        if (symmetry.getType() == SymmetryType.VLINE) super.addStartingPoint(width - x, y);
        else if (symmetry.getType() == SymmetryType.POINT) super.addStartingPoint(width - x, height - y);
    }

    @Override
    public Edge addEndingPoint(int x, int y) {
        Edge edge1 = super.addEndingPoint(x, y);
        Edge edge2 = null;
        if (symmetry.getType() == SymmetryType.VLINE) edge2 = super.addEndingPoint(width - x, y);
        else if (symmetry.getType() == SymmetryType.POINT)
            edge2 = super.addEndingPoint(width - x, height - y);

        // edge1 and edge2 are already opposite each other (check super.addEndingPoint)
        if (edge2 != null) {
            oppositeVertex.put(edge1.to.index, edge2.to);
            oppositeVertex.put(edge2.to.index, edge1.to);

            oppositeEdge.put(edge1.index, edge2);
            oppositeEdge.put(edge2.index, edge1);
        }
        return edge1;
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
