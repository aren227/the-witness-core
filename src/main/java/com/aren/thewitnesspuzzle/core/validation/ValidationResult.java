package com.aren.thewitnesspuzzle.core.validation;

import com.aren.thewitnesspuzzle.core.cursor.area.Area;
import com.aren.thewitnesspuzzle.core.rules.EliminationRule;
import com.aren.thewitnesspuzzle.core.rules.RuleBase;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    public List<RuleBase> notOnAreaErrors = new ArrayList<>();
    public List<Area.AreaValidationResult> areaValidationResults = new ArrayList<>();
    public boolean forceFail = false;
    public boolean timedOut = false;

    public boolean failed() {
        if (timedOut || forceFail) return true;
        if (notOnAreaErrors.size() > 0) return true;
        for (Area.AreaValidationResult result : areaValidationResults) {
            if (!result.eliminated && result.originalErrors.size() > 0) return true;
            if (result.eliminated && result.newErrors.size() > 0) return true;
        }
        return false;
    }

    public boolean hasEliminatedRule() {
        if (timedOut) return false;
        for (Area.AreaValidationResult result : areaValidationResults) {
            if (result.eliminated) return true;
        }
        return false;
    }

    public List<RuleBase> getEliminatedRules() {
        List<RuleBase> rules = new ArrayList<>();
        for (Area.AreaValidationResult result : areaValidationResults) {
            for (RuleBase rule : result.area.getAllRules()) {
                if(rule.eliminated){
                    rules.add(rule);
                }
            }
                /*for (Rule rule : result.originalErrors) {
                    if (rule.eliminated) {
                        rules.add(rule);
                    }
                }*/
        }
        return rules;
    }

    public List<RuleBase> getEliminators() {
        List<RuleBase> rules = new ArrayList<>();
        for (Area.AreaValidationResult result : areaValidationResults) {
            for (RuleBase rule : result.area.getAllRules()) {
                if (rule instanceof EliminationRule) {
                    rules.add(rule);
                }
            }
        }
        return rules;
    }

    public List<RuleBase> getOriginalErrors() {
        List<RuleBase> rules = new ArrayList<>(notOnAreaErrors);
        for (Area.AreaValidationResult result : areaValidationResults) {
            rules.addAll(result.originalErrors);
        }
        return rules;
    }

    public List<RuleBase> getNewErrors() {
        List<RuleBase> rules = new ArrayList<>(notOnAreaErrors);
        for (Area.AreaValidationResult result : areaValidationResults) {
            if (result.eliminated) rules.addAll(result.newErrors);
            else rules.addAll(result.originalErrors);
        }
        return rules;
    }

}
