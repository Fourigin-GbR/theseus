package com.fourigin.apps.theseus.prototype;

import com.fourigin.theseus.models.ClassificationType;
import com.fourigin.theseus.repository.ModelObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/classificationType")
public class ClassificationTypeController {

    private ModelObjectRepository modelObjectRepository;

    /**
     * /classificationType/_all
     * @param sort optional parameter to sort result list
     * @return the list of all available codes.
     */
    @RequestMapping(value = "_all", method = RequestMethod.GET)
    @ResponseBody
    public List<String> retrieveAllCodes(@RequestParam(required = false) boolean sort){
        Set<String> classificationTypes = modelObjectRepository.getAllIds(ClassificationType.class);
        if(classificationTypes == null){
            return null;
        }

        List<String> result = new ArrayList<>(classificationTypes);

        if(sort) {
            Collections.sort(result);
        }

        return result;
    }

    /**
     * /classificationType?code={code}
     * @param code classificationType code
     * @return the classificationType model object.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ClassificationType> retrieve(@RequestParam List<String> code){

        Map<String, ClassificationType> entries = modelObjectRepository.retrieve(ClassificationType.class, code);
        if(entries == null){
            throw new ObjectNotFound("Error retrieving classification type(s): no classification type found for id(s) '" + code + "'!");
        }

        List<ClassificationType> result = new ArrayList<>(entries.size());
        for (String c : code) {
            ClassificationType entry = entries.get(c);
            if(entry != null) {
                result.add(entry);
            }
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void update(@RequestBody ClassificationType classificationType){
        modelObjectRepository.update(classificationType);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void create(@RequestBody ClassificationType classificationType){
        modelObjectRepository.create(classificationType);
    }

    /**
     * /classificationType?id={id}
     * @param code classificationType id
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestParam String code){
        modelObjectRepository.delete(ClassificationType.class, code);
    }

    /**
     * /classificationType/_filter?type={type}&sort
     * @param type type of requested classifications
     * @param sort optional parameter to sort result list
     * @return the filtered list of classifications.
     */
    @RequestMapping(value = "_filter", method = RequestMethod.GET)
    @ResponseBody
    public List<String> retrieveFiltered(
      @RequestParam String type,
      @RequestParam(required = false) boolean sort
    ){

//        Set<String> ids = modelObjectRepository.findIds(ClassificationType.class, new ClassificationFilter.Builder()
//          .forType(type)
//          .build()
//        );
//
//        if(ids.isEmpty()){
//            throw new ObjectNotFound("No classifications found by type '" + type + "'!");
//        }
//
//        List<String> result = new ArrayList<>(ids);
//
//        if(sort){
//            Collections.sort(result);
//        }
//
//        return result;
        return null;
    }

    @Autowired
    public void setModelObjectRepository(ModelObjectRepository modelObjectRepository) {
        this.modelObjectRepository = modelObjectRepository;
    }
}