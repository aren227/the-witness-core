package com.aren.thewitnesspuzzle.core.cursor;

import com.aren.thewitnesspuzzle.core.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.core.rules.RemoveEdgeRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.core.rules.SymmetryColor;

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

        if (edge.getRule() instanceof RemoveEdgeRule || oppositeEdge.getRule() instanceof RemoveEdgeRule) {
            if (from <= 0.5f) to = 0f;
            else to = 1f;
        }

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

    @Override
    public boolean partiallyContainsEdge(Edge edge) {
        return containsEdge(edge) || (currentCursorEdge != null && (currentCursorEdge.edge == edge || ((GridSymmetryPuzzle) puzzle).getOppositeEdge(currentCursorEdge.edge) == edge));
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
