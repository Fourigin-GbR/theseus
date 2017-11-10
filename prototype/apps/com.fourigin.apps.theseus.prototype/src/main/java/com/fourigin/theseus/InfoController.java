package com.fourigin.theseus;

import com.fourigin.theseus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system")
public class InfoController {

    private ProductService productService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Map<String, Object> getInfo(){
        return productService.getInfo();
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public Map<String, Object> getSettings(){
        Map<String, Object> settings = new HashMap<>();

        settings.put("product.views", "ALL");   // THUMBS, LIST, ALL
        settings.put("product.master-enabled", true);
        settings.put("product.changes-enabled", false);
        settings.put("product.page-sizes", Arrays.asList(20, 60, 100));
        settings.put("currencies", Arrays.asList("EUR", "USD", "RUB", "UAH"));
        settings.put("languages", Arrays.asList("en", "de", "ru", "ua", "fr"));

        return settings;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
