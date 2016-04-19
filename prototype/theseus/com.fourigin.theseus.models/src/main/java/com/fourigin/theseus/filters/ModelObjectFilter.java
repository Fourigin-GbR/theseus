package com.fourigin.theseus.filters;

import com.fourigin.theseus.models.ModelObject;

import java.util.ArrayList;
import java.util.List;

public class ModelObjectFilter<T extends ModelObject> {
    private List<ModelObjectFilterEntity> entities = new ArrayList<>();

    public void addEntity(ModelObjectFilterEntity entity){
        if(entity == null){
            return;
        }

        entities.add(entity);
    }

    public void setEntities(List<ModelObjectFilterEntity> entities) {
        this.entities = entities;
    }

    public List<ModelObjectFilterEntity> getEntities() {
        return new ArrayList<>(entities);
    }

    public void clear() {
        entities.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelObjectFilter)) return false;

        ModelObjectFilter<?> that = (ModelObjectFilter<?>) o;

        return entities != null ? entities.equals(that.entities) : that.entities == null;

    }

    @Override
    public int hashCode() {
        return entities != null ? entities.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ModelObjectFilter{" +
          "entities=" + entities +
          '}';
    }
}