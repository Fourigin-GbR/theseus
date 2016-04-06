package com.fourigin.theseus.repository;

import com.fourigin.theseus.models.ModelObject;

import java.util.Collection;

public interface ModelObjectRepository extends ModelObjectResolver {

    <T extends ModelObject> void create(T modelObject);
    <T extends ModelObject> void create(Collection<T> modelObjects);

    <T extends ModelObject> void update(T modelObject);
    <T extends ModelObject> void update(Collection<T> modelObjects);

    <T extends ModelObject> void delete(Class<T> modelClass, String id);
    <T extends ModelObject> void delete(Class<T> modelClass, Collection<String> ids);

}