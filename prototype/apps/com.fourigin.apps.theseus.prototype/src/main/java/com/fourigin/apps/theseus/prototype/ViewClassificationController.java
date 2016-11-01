package com.fourigin.apps.theseus.prototype;

import com.fourigin.theseus.repository.ModelObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static com.fourigin.apps.theseus.prototype.ClassificationController.retrieveClassificationCodes;

@Controller
@RequestMapping("/view/classification")
public class ViewClassificationController {

    private ModelObjectRepository modelObjectRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String overview(Model model){
        List<String> codes = retrieveClassificationCodes(modelObjectRepository, true);

        model.addAttribute("all", codes);

        return "classifications";
    }

    @Autowired
    public void setModelObjectRepository(ModelObjectRepository modelObjectRepository) {
        this.modelObjectRepository = modelObjectRepository;
    }
}