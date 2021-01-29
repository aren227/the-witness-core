package com.aren.thewitnesspuzzle.core.validation;

import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.Area;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
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

            // Validate rules on the line like hexagon.
            // Note that rules on the line are not included in any area.
            // It's slow, but can contain edges or vertices not adjoining valid tiles.
            List<RuleBase> rules = new ArrayList<>();
            for (Vertex vertex : puzzleBase.getVertices()) {
                if (vertex.getRule() == null)
                    continue;

                boolean notIncluded = true;
                for (Area area : splitter.areaList) {
                    if (area.vertices.contains(vertex)) {
                        notIncluded = false;
                        break;
                    }
                }

                if (notIncluded)
                    rules.add(vertex.getRule());
            }
            for (Edge edge : puzzleBase.getEdges()) {
                if (edge.getRule() == null)
                    continue;

                boolean notIncluded = true;
                for (Area area : splitter.areaList) {
                    if (area.edges.contains(edge)) {
                        notIncluded = false;
                        break;
                    }
                }

                if (notIncluded)
                    rules.add(edge.getRule());
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
