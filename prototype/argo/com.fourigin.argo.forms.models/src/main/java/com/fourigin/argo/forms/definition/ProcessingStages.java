package com.fourigin.argo.forms.definition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessingStages {
    private Map<String, ProcessingStage> stages;

    private Map<String, Integer> nameIndexOrder;

    private Map<Integer, String> indexNameOrder;

    public ProcessingStages(List<ProcessingStage> allStages) {
        if (allStages == null || allStages.isEmpty()) {
            stages = Collections.emptyMap();
            nameIndexOrder = Collections.emptyMap();
            indexNameOrder = Collections.emptyMap();
            return;
        }

        stages = new HashMap<>();
        nameIndexOrder = new HashMap<>();
        indexNameOrder = new HashMap<>();
        for (int i = 0; i < allStages.size(); i++) {
            ProcessingStage stage = allStages.get(i);
            String stageName = stage.getName();

            stages.put(stageName, stage);
            nameIndexOrder.put(stageName, i);
            indexNameOrder.put(i, stageName);
        }
    }

    public ProcessingStage first(){
        String firstName = indexNameOrder.get(0);
        return stages.get(firstName);
    }

    public String firstName(){
        return indexNameOrder.get(0);
    }

    public ProcessingStage find(String name) {
        return stages.get(name);
    }

    public ProcessingStage getNext(String currentName) {
        if (!nameIndexOrder.containsKey(currentName)) {
            throw new IllegalArgumentException("No stage exist for name '" + currentName + "'!");
        }

        int currentPos = nameIndexOrder.get(currentName);
        String nextName = indexNameOrder.get(currentPos + 1);

        return stages.get(nextName);
    }

    public boolean isLast(String name) {
        if (!nameIndexOrder.containsKey(name)) {
            throw new IllegalArgumentException("No stage exist for name '" + name + "'!");
        }

        int currentPos = nameIndexOrder.get(name);
        return currentPos == stages.size() - 1;
    }
}
