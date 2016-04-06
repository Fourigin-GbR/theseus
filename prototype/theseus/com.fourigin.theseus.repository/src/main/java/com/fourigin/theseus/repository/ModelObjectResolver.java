package com.fourigin.theseus.repository;

import com.fourigin.theseus.filters.ClassificationFilter;
import com.fourigin.theseus.filters.ModelObjectFilter;
import com.fourigin.theseus.models.ClassificationModel;
import com.fourigin.theseus.models.ModelObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

interface ModelObjectResolver {

    <T extends ModelObject> Set<String> getAllIds(Class<T> modelClass);

    <T extends ModelObject> T retrieve(Class<T> modelClass, String id);
    <T extends ModelObject> Map<String, T> retrieve(Class<T> modelClass, Collection<String> ids);

    <T extends ModelObject> Set<String> findIds(Class<T> modelClass, ModelObjectFilter<T> filter);
    <T extends ModelObject> Set<T> find(Class<T> modelClass, ModelObjectFilter<T> filter);

}