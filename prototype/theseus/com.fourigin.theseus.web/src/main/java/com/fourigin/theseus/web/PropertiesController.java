package com.fourigin.theseus.web;

import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.types.PropertyType;
import com.fourigin.theseus.repository.filter.PropertySearchFilter;
import com.fourigin.theseus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/property")
public class PropertiesController {

    private ProductService productService;

    @RequestMapping(value = "/_codes", method = RequestMethod.GET)
    public List<String> retrieveCodes(){
        return productService.findPropertyCodes(PropertySearchFilter.forSearchKey(null));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public PropertyDefinitionsResponse retrievePropertyDefinitions(@RequestParam("code") List<String> codes){
        Map<String, PropertyDefinition<? extends PropertyType>> definitions = productService.resolvePropertyDefinitions(codes);
        return convert(definitions);
    }

    @RequestMapping(value = "/_range", method = RequestMethod.GET)
    public PropertyDefinitionsResponse retrieveProductRange(
        @RequestParam("limit") int limit,
        @RequestParam(value = "offset", required = false, defaultValue = "0") int offset
    ){
        if(limit <= 0){
            throw new IllegalArgumentException("limit must be positive!");
        }
        if(offset < 0){
            throw new IllegalArgumentException("offset must not be negative!");
        }

        List<String> allCodes = productService.findPropertyCodes(PropertySearchFilter.forSearchKey(null));
        int size = allCodes.size();

        if(offset > size){
            throw new IllegalArgumentException("offset must not be greater then the total amount of properties (" + size + ")!");
        }

        int endPos = offset + limit;
        if(endPos > size){
            endPos = size;
        }

        List<String> matchingCodes = allCodes.subList(offset, endPos);

        Map<String, PropertyDefinition<? extends PropertyType>> propertyDefinitions = productService.resolvePropertyDefinitions(matchingCodes);

        return convert(propertyDefinitions);
    }
    
    private PropertyDefinitionsResponse convert(Map<String, PropertyDefinition<? extends PropertyType>> definitions){
        List<Property> props = new ArrayList<>();
        Set<PropertyType> types = new HashSet<>();
        for (Map.Entry<String, PropertyDefinition<? extends PropertyType>> entry : definitions.entrySet()) {
            String code = entry.getKey();
            PropertyDefinition<? extends PropertyType> definition = entry.getValue();
            PropertyType type = definition.getPropertyType();

            Property prop = new Property();
            prop.setCode(code);
            prop.setType(type.getName());
            prop.setName(definition.getName());

            props.add(prop);
            types.add(type);
        }

        return new PropertyDefinitionsResponse(props, types);
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}