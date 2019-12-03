package com.fourigin.argo.repository.action;

import com.fourigin.argo.models.action.Action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class InMemoryActionRepository implements ActionRepository {

    private List<ActionEntry> entries = new ArrayList<>();

    @Override
    public void addAction(String revision, Action action) {
        entries.add(new ActionEntry(revision, action));
    }

    @Override
    public List<Action> resolveDiff(String startRevision, String endRevision) {
        Objects.requireNonNull(startRevision, "startRevision must not be null!");
        Objects.requireNonNull(endRevision, "endRevision must not be null!");

        List<Action> result = new ArrayList<>();

        boolean foundStart = false;
        for (ActionEntry entry : entries) {
            String currentRevision = entry.revision;
            if (!foundStart) {
                if (startRevision.equals(currentRevision)) {
                    foundStart = true;
                    result.add(entry.action);
                }
                continue;
            }

            result.add(entry.action);
            if (endRevision.equals(currentRevision)) {
                break;
            }
        }

        return result;
    }

    private static class ActionEntry {
        long timestamp;
        String revision;
        Action action;

        ActionEntry(String revision, Action action) {
            this.timestamp = new Date().getTime();
            this.revision = revision;
            this.action = action;
        }
    }
}
