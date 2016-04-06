package com.fourigin.theseus.filters;

import com.fourigin.theseus.models.ClassificationModel;

public class ClassificationModelFilter extends ModelObjectFilter<ClassificationModel> {

    public static class Builder {
        private ClassificationModelFilter instance = new ClassificationModelFilter();

        public ClassificationModelFilter.Builder forType(String type){
            instance.addEntity(new ModelObjectFilterEntity("type", type, FilterEntityRelation.EQUALS));
            return this;
        }

        public ClassificationModelFilter build(){
            return instance;
        }
    }

}