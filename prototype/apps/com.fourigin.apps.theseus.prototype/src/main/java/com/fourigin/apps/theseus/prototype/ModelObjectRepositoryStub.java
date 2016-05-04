package com.fourigin.apps.theseus.prototype;

import com.fourigin.theseus.filters.ModelObjectFilter;
import com.fourigin.theseus.filters.ModelObjectFilterEntity;
import com.fourigin.theseus.models.Classification;
import com.fourigin.theseus.models.ClassificationType;
import com.fourigin.theseus.models.ModelObject;
import com.fourigin.theseus.repository.ModelObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelObjectRepositoryStub implements ModelObjectRepository {
    private Map<String, Classification> classifications;
    private Map<String, ClassificationType> classificationTypes;

    private final Logger logger = LoggerFactory.getLogger(ModelObjectRepositoryStub.class);

    @Override
    public <T extends ModelObject> Set<String> getAllIds(Class<T> modelClass) {
        Map<String, ? extends ModelObject> map = getInternalMap(modelClass);
        if(map == null){
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelClass);
            return Collections.emptySet();
        }

        return map.keySet();
    }

    @Override
    public <T extends ModelObject> T retrieve(Class<T> modelClass, String id) {
        Map<String, ? extends T> map = getInternalMap(modelClass);
        if(map == null || map.isEmpty()){
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelClass);
            return null;
        }

        return map.get(id);
    }

    @Override
    public <T extends ModelObject> Map<String, T> retrieve(Class<T> modelClass, Collection<String> ids) {
        Map<String, ? extends T> map = getInternalMap(modelClass);
        if(map == null || map.isEmpty()){
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelClass);
            return Collections.emptyMap();
        }

        Map<String, T> result = new HashMap<>();
        for (String id : ids) {
            T modelObject = map.get(id);
            if(modelObject == null){
                if (logger.isWarnEnabled()) logger.warn("No model object found for type {} and id '{}'", modelClass, id);
            }
            result.put(id, modelObject);
        }

        return result;
    }

    @Override
    public <T extends ModelObject> Set<String> findIds(Class<T> modelClass, ModelObjectFilter<T> filter) {
        List<ModelObjectFilterEntity> entities = filter.getEntities();
        if (entities.isEmpty()) {
            if (logger.isErrorEnabled()) logger.error("No filter entities specified!");
            return Collections.emptySet();
        }

        Map<String, ? extends T> map = getInternalMap(modelClass);
        if (map == null || map.isEmpty()) {
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelClass);
            return Collections.emptySet();
        }

        Set<T> values = new HashSet<>(map.values());

        Set<T> filteredValues = filter(values, entities, modelClass);

        return filteredValues.stream().map(ModelObject::getId).collect(Collectors.toSet());
    }

    @Override
    public <T extends ModelObject> Set<T> find(Class<T> modelClass, ModelObjectFilter<T> filter) {
        List<ModelObjectFilterEntity> entities = filter.getEntities();
        if (entities.isEmpty()) {
            if (logger.isErrorEnabled()) logger.error("No filter entities specified!");
            return Collections.emptySet();
        }

        Map<String, ? extends T> map = getInternalMap(modelClass);
        if (map == null || map.isEmpty()) {
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelClass);
            return Collections.emptySet();
        }

        return filter(map.values(), entities, modelClass);
    }

    private <T extends ModelObject> Set<T> filter(Collection<? extends T> objects, List<ModelObjectFilterEntity> entities, Class<T> modelClass){
        Set<String> allIds = new HashSet<>();

        for (ModelObjectFilterEntity entity : entities) {
            String fieldName = entity.getField();
            String filterValue = entity.getFilterValue();

            Field field;
            try {
                field = modelClass.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (NoSuchFieldException ex) {
                if (logger.isErrorEnabled()) logger.error("No field '{}' found in class '{}'!", fieldName, modelClass);
                continue;
            }

            Set<String> ids = new HashSet<>();

            for (T t : objects) {
                try {
                    Object value = field.get(t);
                    switch (entity.getRelation()) {
                        case EQUALS:
                            if (value.equals(filterValue)) {
                                ids.add(t.getId());
                            }
                            break;
                        case CONTAINS:
                            if (String.valueOf(value).contains(filterValue)) {
                                ids.add(t.getId());
                            }
                            break;
                        case STARTS_WITH:
                            if (String.valueOf(value).startsWith(filterValue)) {
                                ids.add(t.getId());
                            }
                            break;
                        case ENDS_WITH:
                            if (String.valueOf(value).endsWith(filterValue)) {
                                ids.add(t.getId());
                            }
                            break;
                        case MATCHES:
                            // TODO
                            break;
                        default:
                            break;
                    }
                } catch (IllegalAccessException ex) {
                    if (logger.isErrorEnabled()) logger.error("Error processing filters!", ex);
                }
            }

            applyIds(allIds, ids);
        }

        return objects.stream().filter(t -> allIds.contains(t.getId())).collect(Collectors.toSet());
    }

    private static void applyIds(Set<String> allIds, Set<String> newIds){
        if(newIds == null || newIds.isEmpty()){
            allIds.clear();
            return;
        }

        allIds.retainAll(newIds);
    }

    @Override
    public <T extends ModelObject> void create(T modelObject) {
        if(modelObject == null){
            if (logger.isErrorEnabled()) logger.error("Unable to create a NULL object!");
            return;
        }

        if(modelObject.getRevision() != null){
            throw new IllegalArgumentException("Unable to create a new model object '" + modelObject + "': revision must be null!");
        }

        Map<String, T> map = getInternalMap(modelObject);
        if(map == null){
            map = new HashMap<>();
            setInternalMap(modelObject, map);
        }


        String id = modelObject.getId();
        T previousObject = map.get(id);
        if(previousObject != null){
            throw new IllegalArgumentException("Unable to create a new model object '" + modelObject + "': the object with id '" + id + "' already available: " + previousObject);
        }

        modelObject.setRevision("1");
        map.put(id, modelObject);
    }

    @Override
    public <T extends ModelObject> void create(Collection<T> modelObjects) {
        if(modelObjects == null || modelObjects.isEmpty()){
            if (logger.isErrorEnabled()) logger.error("Unable to create a NULL object!");
            return;
        }

        T modelObject = modelObjects.iterator().next();
        Map<String, T> map = getInternalMap(modelObject);
        if(map == null){
            map = new HashMap<>();
            setInternalMap(modelObject, map);
        }

        for (T object : modelObjects) {
            if(object.getRevision() != null){
                throw new IllegalArgumentException("Unable to create a new model object '" + object + "': revision must be null!");
            }

            String id = object.getId();
            T previousObject = map.get(id);
            if(previousObject != null){
                throw new IllegalArgumentException("Unable to create a new model object '" + object + "': the object with id '" + id + "' already available: " + previousObject);
            }

            object.setRevision("1");
            map.put(id, object);
        }
    }

    @Override
    public <T extends ModelObject> void update(T modelObject) {
        if(modelObject == null){
            if (logger.isErrorEnabled()) logger.error("No objects to create specified!");
            return;
        }

        Map<String, T> map = getInternalMap(modelObject);
        if(map == null || map.isEmpty()){
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelObject.getClass());
            return;
        }

        String revision = modelObject.getRevision();
        if(revision == null){
            throw new IllegalArgumentException("Unable to update a model object '" + modelObject + "': revision must not be null!");
        }

        String id = modelObject.getId();
        T previousObject = map.get(id);
        if(previousObject == null){
            throw new IllegalArgumentException("Unable to update a model object '" + modelObject + "': the object with id '" + id + "' not found!");
        }

        modelObject.setRevision(String.valueOf(Long.parseLong(revision) + 1));
        map.put(id, modelObject);
    }

    @Override
    public <T extends ModelObject> void update(Collection<T> modelObjects) {
        if(modelObjects == null || modelObjects.isEmpty()){
            if (logger.isErrorEnabled()) logger.error("No objects to update specified!");
            return;
        }

        T modelObject = modelObjects.iterator().next();
        Map<String, T> map = getInternalMap(modelObject);
        if(map == null || map.isEmpty()){
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelObject.getClass());
            return;
        }

        for (T object : modelObjects) {
            String revision = object.getRevision();
            if(revision == null){
                throw new IllegalArgumentException("Unable to update a model object '" + object + "': revision must not be null!");
            }

            String id = object.getId();
            T previousObject = map.get(id);
            if(previousObject == null){
                throw new IllegalArgumentException("Unable to update a model object '" + object + "': the object with id '" + id + "' not found!");
            }

            object.setRevision(String.valueOf(Long.parseLong(revision) + 1));
            map.put(id, object);
        }
    }

    @Override
    public <T extends ModelObject> void delete(Class<T> modelClass, String id) {
        if(id == null){
            if (logger.isErrorEnabled()) logger.error("Unable to delete an object with id NULL!");
            return;
        }

        Map<String, T> map = getInternalMap(modelClass);
        if(map == null || map.isEmpty()){
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelClass);
            return;
        }

        if(!map.containsKey(id)){
            if (logger.isWarnEnabled()) logger.warn("No model object found for id '{}', unable to delete it!", id);
            return;
        }

        map.remove(id);
    }

    @Override
    public <T extends ModelObject> void delete(Class<T> modelClass, Collection<String> ids) {
        if(ids == null || ids.isEmpty()){
            if (logger.isErrorEnabled()) logger.error("No objects to delete specified!");
            return;
        }

        Map<String, T> map = getInternalMap(modelClass);
        if(map == null || map.isEmpty()){
            if (logger.isWarnEnabled()) logger.warn("No model objects available for class {}", modelClass);
            return;
        }

        for (String id : ids) {
            if(!map.containsKey(id)){
                if (logger.isWarnEnabled()) logger.warn("No model object found for id '{}', unable to delete it!", id);
                continue;
            }

            map.remove(id);
        }
    }

    private <T extends ModelObject> Map<String, T> getInternalMap(Class<T> modelClass){
        if(Classification.class.isAssignableFrom(modelClass)){
            //noinspection unchecked
            return (Map<String, T>) classifications;
        }

        if(ClassificationType.class.isAssignableFrom(modelClass)){
            //noinspection unchecked
            return (Map<String, T>) classificationTypes;
        }

        return null;
    }

    private <T extends ModelObject> Map<String, T> getInternalMap(T modelObject){
        if(Classification.class.isAssignableFrom(modelObject.getClass())){
            //noinspection unchecked
            return (Map<String, T>) classifications;
        }

        if(ClassificationType.class.isAssignableFrom(modelObject.getClass())){
            //noinspection unchecked
            return (Map<String, T>) classificationTypes;
        }

        return null;
    }

    private <T extends ModelObject> void setInternalMap(T modelObject, Map<String, T> map){
        if(Classification.class.isAssignableFrom(modelObject.getClass())){
            //noinspection unchecked
            classifications = (Map<String, Classification>) map;
        }

        if(ClassificationType.class.isAssignableFrom(modelObject.getClass())){
            //noinspection unchecked
            classificationTypes = (Map<String, ClassificationType>) map;
        }
    }
}
