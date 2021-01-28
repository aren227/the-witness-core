package com.aren.thewitnesspuzzle.core.validation;

import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.Area;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.core.puzzle.PuzzleBase;
import com.aren.thewitnesspuzzle.core.rules.RuleBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PuzzleValidator {

    public static ValidationResult validate(Cursor cursor, PuzzleBase puzzleBase) {
        if (puzzleBase instanceof GridPuzzle) {
            final GridAreaSplitter splitter = new GridAreaSplitter(cursor);
            final ValidationResult result = new ValidationResult();

            ExecutorService executorService = Executors.newCachedThreadPool();
            Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws InterruptedException {
                    for (Area area : splitter.areaList) {
                        result.areaValidationResults.add(area.validate(cursor));
                    }
                    return null;
                }
            };
            Future<Void> future = executorService.submit(task);
            try {
                future.get(5000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                result.timedOut = true;
            } finally {
                future.cancel(true);
            }

            if (result.timedOut)
                return result;

            //FIXME: Dirty code again. I think getVisitedVerticies() of SymmetryCursor should return with opposite vertices.
            List<RuleBase> rules = new ArrayList<>();
            for (Vertex vertex : cursor.getVisitedVertices()) {
                if (vertex.getRule() != null) rules.add(vertex.getRule());
                if (puzzleBase instanceof GridSymmetryPuzzle) {
                    Vertex opposite = ((GridSymmetryPuzzle) puzzleBase).getOppositeVertex(vertex);
                    if (opposite.getRule() != null) rules.add(opposite.getRule());
                }
            }
            for (Edge edge : cursor.getFullyVisitedEdges()) {
                if (edge.getRule() != null) rules.add(edge.getRule());
                if (puzzleBase instanceof GridSymmetryPuzzle) {
                    Edge opposite = ((GridSymmetryPuzzle) puzzleBase).getOppositeEdge(edge);
                    if (opposite.getRule() != null) rules.add(opposite.getRule());
                }
            }

            for (RuleBase rule : rules)
                rule.eliminated = false;

            for (RuleBase rule : rules) {
                if (!rule.validateLocally(cursor)) {
                    result.notOnAreaErrors.add(rule);
                }
            }

            return result;
        } else {
            ValidationResult result = new ValidationResult();
            //TODO: Support area validation
            for (RuleBase rule : puzzleBase.getAllRules()) {
                if (!rule.validateLocally(cursor)) {
                    result.notOnAreaErrors.add(rule);
                }
            }
            return result;
        }
    }

    public static boolean validate(Cursor cursor, List<Integer> pattern) {
        List<Integer> visited = cursor.getVisitedVertexIndices();
        if (pattern.size() != visited.size()) {
            return false;
        }
        for (int i = 0; i < pattern.size(); i++) {
            if (!pattern.get(i).equals(visited.get(i))) {
                return false;
            }
        }
        return true;
    }

}
