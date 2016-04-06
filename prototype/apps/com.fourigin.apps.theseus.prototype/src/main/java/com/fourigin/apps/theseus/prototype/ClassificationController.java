package com.fourigin.apps.theseus.prototype;

import com.fourigin.theseus.filters.ClassificationModelFilter;
import com.fourigin.theseus.models.ClassificationModel;
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
import java.util.Set;

@RestController
@RequestMapping("/classification")
public class ClassificationController {

    private ModelObjectRepository modelObjectRepository;

    /**
     * /classification/_all
     * @param sort optional parameter to sort result list
     * @return the list of all available codes.
     */
    @RequestMapping(value = "_all", method = RequestMethod.GET)
    @ResponseBody
    public List<String> retrieveAllCodes(@RequestParam(required = false) boolean sort){
        Set<String> classifications = modelObjectRepository.getAllIds(ClassificationModel.class);
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
     * /classification?id={id}
     * @param code classification id
     * @return the classification model object.
     */
    @RequestMapping(value = "{code}", method = RequestMethod.GET)
    @ResponseBody
    public ClassificationModel retrieve(@RequestParam String code){
        ClassificationModel result = modelObjectRepository.retrieve(ClassificationModel.class, code);
        if(result == null){
            throw new ObjectNotFound("Error retrieving classification: no classification found for id '" + code + "'!");
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void update(@RequestBody ClassificationModel classificationModel){
        modelObjectRepository.update(classificationModel);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void create(@RequestBody ClassificationModel classificationModel){
        modelObjectRepository.create(classificationModel);
    }

    /**
     * /classification?id={id}
     * @param code classification id
     */
    @RequestMapping(value = "{code}", method = RequestMethod.DELETE)
    public void delete(@RequestParam String code){
        modelObjectRepository.delete(ClassificationModel.class, code);
    }

    /**
     * /classification/_filter?type={type}&sort
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

        Set<String> ids = modelObjectRepository.findIds(ClassificationModel.class, new ClassificationModelFilter.Builder()
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