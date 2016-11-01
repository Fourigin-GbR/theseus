package com.fourigin.apps.theseus.prototype;

import com.fourigin.theseus.filters.ClassificationFilter;
import com.fourigin.theseus.models.Classification;
import com.fourigin.theseus.repository.ModelObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/classification")
public class ClassificationController {

    private ModelObjectRepository modelObjectRepository;

    public static List<String> retrieveClassificationCodes(ModelObjectRepository modelObjectRepository, boolean sort){
        Set<String> classifications = modelObjectRepository.getAllIds(Classification.class);
        if(classifications == null){
            return null;
        }

        List<String> result = new ArrayList<>(classifications);

        if(sort) {
            Collections.sort(result);
        }

        return result;
    }

    /**
     * /classification/_all
     * @param sort optional parameter to sort result list
     * @return the list of all available codes.
     */
    @RequestMapping(value = "_all", method = RequestMethod.GET)
//    @ResponseBody
    public List<String> retrieveAllCodes(@RequestParam(required = false) boolean sort){
        return retrieveClassificationCodes(modelObjectRepository, sort);
    }


    /**
     * /classification?code={code}
     * @param code classification code
     * @return the classification model object.
     */
    @RequestMapping(method = RequestMethod.GET)
//    @ResponseBody
    public List<Classification> retrieve(@RequestParam List<String> code){

        Map<String, Classification> entries = modelObjectRepository.retrieve(Classification.class, code);
        if(entries == null){
            throw new ObjectNotFound("Error retrieving classification(s): no classification found for id(s) '" + code + "'!");
        }

        List<Classification> result = new ArrayList<>(entries.size());
        for (String c : code) {
            Classification entry = entries.get(c);
            if(entry != null) {
                result.add(entry);
            }
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void update(@RequestBody Classification classificationModel){
        modelObjectRepository.update(classificationModel);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void create(@RequestBody Classification classificationModel){
        modelObjectRepository.create(classificationModel);
    }

    /**
     * /classification?code={id}
     * @param code classification id
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestParam String code){
        modelObjectRepository.delete(Classification.class, code);
    }

    /**
     * /classification/_filter?type={type}&sort
     * @param type type of requested classifications
     * @param sort optional parameter to sort result list
     * @return the filtered list of classifications.
     */
    @RequestMapping(value = "_filter", method = RequestMethod.GET)
//    @ResponseBody
    public List<String> retrieveFiltered(
      @RequestParam String type,
      @RequestParam(required = false) boolean sort
    ){

        Set<String> ids = modelObjectRepository.findIds(Classification.class, new ClassificationFilter.Builder()
          .forType(type)
          .build()
        );

        if(ids.isEmpty()){
            throw new ObjectNotFound("No classifications found by type '" + type + "'!");
        }

        List<String> result = new ArrayList<>(ids);

        if(sort){
            Collections.sort(result);
        }

        return result;
    }

    @Autowired
    public void setModelObjectRepository(ModelObjectRepository modelObjectRepository) {
        this.modelObjectRepository = modelObjectRepository;
    }
}