package com.fourigin.theseus.filters;

import com.fourigin.theseus.models.Classification;

import java.util.ArrayList;
import java.util.List;

public class ClassificationFilter extends ModelObjectFilter<Classification> {

    public static class Builder {
        private List<ModelObjectFilterEntity> entities = new ArrayList<>();

        public ClassificationFilter.Builder forType(String type){
            entities.add(new ModelObjectFilterEntity("typeCode", type, FilterEntityRelation.EQUALS));
            return this;
        }

        public ClassificationFilter build(){
            ClassificationFilter result = new ClassificationFilter();
            result.setEntities(new ArrayList<>(entities));

            entities.clear();

            return result;
        }
    }

}