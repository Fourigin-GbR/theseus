package com.fourigin.argo.forms.prepopulation;

import com.fourigin.argo.forms.models.FormsStoreEntry;

import java.util.Map;
import java.util.Objects;

public class StoredEntryPrePopulationValueResolver implements PrePopulationValuesResolver {
    private String resolveName;

    public StoredEntryPrePopulationValueResolver(String resolveName) {
        this.resolveName = resolveName;
    }

    @Override
    public boolean isAccepted(PrePopulationType type) {
        return PrePopulationType.ENTRY == type;
    }

    @Override
    public String getName() {
        return resolveName;
    }

    @Override
    public Map<String, String> resolveValues(Object context) {
        Objects.requireNonNull(context, "context must not be null!");

        if (!FormsStoreEntry.class.isAssignableFrom(context.getClass())) {
            throw new IllegalArgumentException("Incompatible class for an ENTRY-Type-Resolver! Required context of type " + FormsStoreEntry.class + ", but found " + context.getClass());
        }

        FormsStoreEntry entry = (FormsStoreEntry) context;

        return entry.getData();
    }
}
