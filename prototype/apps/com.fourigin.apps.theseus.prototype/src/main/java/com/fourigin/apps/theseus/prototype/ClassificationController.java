package com.fourigin.apps.theseus.prototype;

import com.fourigin.theseus.models.ClassificationModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classification")
public class ClassificationController {

    private Map<String, ClassificationModel> classifications = new HashMap<>();

    @PostConstruct
    public void init(){
        ClassificationModel.Builder builder = new ClassificationModel.Builder();

        classifications.put("c140", builder.code("c140").typeCode("model").revision("123").description("Captiva (C140)").build());
        classifications.put("1yy", builder.code("1yy").typeCode("model").revision("312").description("Corvette Stingray (1YY)").build());
        classifications.put("1yz", builder.code("1yz").typeCode("model").revision("231").description("Corvette Z06 (1YZ)").build());
        classifications.put("ls", builder.code("ls").typeCode("trim").revision("213").description("LS").build());
        classifications.put("lt", builder.code("lt").typeCode("trim").revision("321").description("LT").build());
        classifications.put("1.8", builder.code("1.8").typeCode("version").revision("132").description("1.8").build());
    }

    @RequestMapping(value = "_all", method = RequestMethod.GET)
    @ResponseBody
    public List<String> retrieveAllCodes(@RequestParam(required = false) boolean sort){
        List<String> result = new ArrayList<>(classifications.keySet());

        if(sort) {
            Collections.sort(result);
        }

        return result;
    }

    @RequestMapping(value = "{code}", method = RequestMethod.GET)
    @ResponseBody
    public ClassificationModel retrieve(@PathVariable String code){
        ClassificationModel result = classifications.get(code);
        if(result == null){
            throw new ObjectNotFound("Error retrieving classification: no classification found for code '" + code + "'!");
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void update(@RequestBody ClassificationModel classificationModel){
        String code = classificationModel.getCode();
        if(!classifications.containsKey(code)){
            throw new ObjectNotFound("Error updating classification: no classification found for code '" + code + "'!");
        }

        classifications.put(classificationModel.getCode(), classificationModel);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void create(@RequestBody ClassificationModel classificationModel){
        String code = classificationModel.getCode();
        if(classifications.containsKey(code)){
            throw new IllegalArgumentException("Error creating classification: a classification with code '" + code + "' already exists!");
        }

        classifications.put(code, classificationModel);
    }

    @RequestMapping(value = "{code}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String code){
        if(!classifications.containsKey(code)){
            throw new ObjectNotFound("Error deleting classification: no classification found for code '" + code + "'!");
        }

        classifications.remove(code);
    }

    @RequestMapping(value = "_filter/byType/{typeCode}", method = RequestMethod.GET)
    @ResponseBody
    public List<String> retrieveByTypeCode(@PathVariable String typeCode, @RequestParam(required = false) boolean sort){
        List<String> result = classifications.values().stream()
          .filter(model -> typeCode.equals(model.getTypeCode()))
          .map(ClassificationModel::getCode)
          .collect(Collectors.toList());

        if(result.isEmpty()){
            throw new ObjectNotFound("No classifications found by type '" + typeCode + "'!");
        }

        if(sort){
            Collections.sort(result);
        }

        return result;
    }

}