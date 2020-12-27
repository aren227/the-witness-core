package aren227.thewitness.core.cursor;

import aren227.thewitness.core.puzzle.GridSymmetryPuzzle;
import aren227.thewitness.core.graph.Edge;
import aren227.thewitness.core.graph.EdgeProportion;
import aren227.thewitness.core.graph.Vertex;
import aren227.thewitness.core.rules.BrokenLineRule;
import aren227.thewitness.core.rules.StartingPointRule;
import aren227.thewitness.core.rules.SymmetryColor;

import java.util.ArrayList;
import java.util.List;

public class SymmetryCursor extends Cursor {

    public SymmetryCursor(GridSymmetryPuzzle puzzle, Vertex start) {
        super(puzzle, start);
    }

    public SymmetryCursor(GridSymmetryPuzzle puzzle, ArrayList<Vertex> vertices, EdgeProportion cursorEdge) {
        super(puzzle, vertices, cursorEdge);
    }

    @Override
    public void updateProportionWithCollision(EdgeProportion edgeProportion, float from, float to) {
        Edge edge = edgeProportion.edge;
        float length = edge.getLength();

        GridSymmetryPuzzle gridSymmetryPuzzle = (GridSymmetryPuzzle) puzzle;

        Edge oppositeEdge = gridSymmetryPuzzle.getOppositeEdge(edge);

        // Broken edge collision check
        if (edge.getRule() instanceof BrokenLineRule || oppositeEdge.getRule() instanceof BrokenLineRule) {
            float radius;
            if (edge.getRule() instanceof BrokenLineRule)
                radius = ((BrokenLineRule) edge.getRule()).getCollisionCircleRadius();
            else radius = ((BrokenLineRule) oppositeEdge.getRule()).getCollisionCircleRadius();

            float collisionProportion = radius + puzzle.getPathWidth() * 0.5f / length;
            if (from <= 0.5f) to = Math.min(0.5f - collisionProportion, to);
            else to = Math.max(0.5f + collisionProportion, to);
        }

        // Cursor self collision check
        for (int i = 0; i < visited.size() - 1; i++) {
            Vertex v = visited.get(i);
            if (edge.containsVertex(v)) {
                float collisionProportion = puzzle.getPathWidth() / length;
                if (v.getRule() instanceof StartingPointRule)
                    collisionProportion = (((StartingPointRule) v.getRule()).getRadius() + puzzle.getPathWidth() * 0.5f) / length;
                to = Math.min(1 - collisionProportion, to);
            }
        }

        // Opposite cursor collision check
        for (int i = 0; i < visited.size() - 1; i++) {
            Vertex v = gridSymmetryPuzzle.getOppositeVertex(visited.get(i));
            if (edge.containsVertex(v)) {
                float collisionProportion = puzzle.getPathWidth() / length;
                if (v.getRule() instanceof StartingPointRule)
                    collisionProportion = (((StartingPointRule) v.getRule()).getRadius() + puzzle.getPathWidth() * 0.5f) / length;
                to = Math.min(1 - collisionProportion, to);
            }
        }

        // Two cursors collided each other
        if (edgeProportion.to() == gridSymmetryPuzzle.getOppositeVertex(edgeProportion.to())) {
            float collisionProportion = puzzle.getPathWidth() * 0.5f / length;
            to = Math.min(1 - collisionProportion, to);
        }
        if (edge == oppositeEdge) {
            float collisionProportion = puzzle.getPathWidth() * 0.5f / length;
            to = Math.min(0.5f - collisionProportion, to);
        }

        edgeProportion.proportion = to;
    }

    public boolean hasSymmetricColor() {
        return getPuzzle().getSymmetry().hasColor();
    }

    @Override
    public boolean containsEdge(Edge edge) {
        List<Edge> edges = getFullyVisitedEdges();
        return edges.contains(edge) || edges.contains(((GridSymmetryPuzzle) puzzle).getOppositeEdge(edge));
    }

    public SymmetryColor getSymmetricColor(Edge edge) {
        if (getFullyVisitedEdges().contains(edge)) return ((GridSymmetryPuzzle) puzzle).getSymmetry().getPrimaryColor();
        return ((GridSymmetryPuzzle) puzzle).getSymmetry().getSecondaryColor();
    }

    @Override
    public boolean containsVertex(Vertex vertex) {
        List<Vertex> vertices = getVisitedVertices();
        return vertices.contains(vertex) || vertices.contains(((GridSymmetryPuzzle) puzzle).getOppositeVertex(vertex));
    }

    public SymmetryColor getSymmetricColor(Vertex vertex) {
        if (getVisitedVertices().contains(vertex)) return ((GridSymmetryPuzzle) puzzle).getSymmetry().getPrimaryColor();
        return ((GridSymmetryPuzzle) puzzle).getSymmetry().getSecondaryColor();
    }

    @Override
    public GridSymmetryPuzzle getPuzzle() {
        return (GridSymmetryPuzzle) puzzle;
    }
}
