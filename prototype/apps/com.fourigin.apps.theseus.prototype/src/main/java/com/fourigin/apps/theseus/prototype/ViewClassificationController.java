package com.fourigin.apps.theseus.prototype;

import com.fourigin.theseus.models.Classification;
import com.fourigin.theseus.repository.ModelObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fourigin.apps.theseus.prototype.ClassificationController.retrieveClassificationCodes;

@Controller
@RequestMapping("/view/classification")
public class ViewClassificationController {

    private ModelObjectRepository modelObjectRepository;

    @RequestMapping(value = "/_codes", method = RequestMethod.GET)
    public String overview(Model model){
        List<String> codes = retrieveClassificationCodes(modelObjectRepository, true);

        model.addAttribute("classificationCodes", codes);

        return "classifications";
    }

    /**
     * /classification?code={code}
     * @param codes classification code
     * @return the classification model object.
     */
    @RequestMapping(method = RequestMethod.GET)
    public String retrieve(@RequestParam List<String> codes, Model model){
        Map<String, Classification> entries = modelObjectRepository.retrieve(Classification.class, codes);
        if(entries == null){
            throw new ObjectNotFound("Error retrieving classification(s): no classification found for code(s) '" + codes + "'!");
        }

        List<Classification> result = new ArrayList<>(entries.size());
        for (String c : codes) {
            Classification entry = entries.get(c);
            if(entry != null) {
                result.add(entry);
            }
        }

        model.addAttribute("classifications", result);
        return "classificationDetails";
    }

    @Autowired
    public void setModelObjectRepository(ModelObjectRepository modelObjectRepository) {
        this.modelObjectRepository = modelObjectRepository;
    }
}