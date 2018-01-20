package com.fourigin.theseus.web;

import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.core.ProductDetails;
import com.fourigin.theseus.core.Property;
import com.fourigin.theseus.core.types.PropertyType;
import com.fourigin.theseus.core.types.PropertyValue;
import com.fourigin.theseus.repository.filter.ProductSearchFilter;
import com.fourigin.theseus.repository.filter.PropertySearchFilter;
import com.fourigin.theseus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductsController {

    private ProductService productService;

    @RequestMapping(value = "/_codes", method = RequestMethod.GET)
    public List<String> retrieveCodes(){
        // TODO: use proper filter
        ProductSearchFilter searchFilter = ProductSearchFilter.forSearchKey(null);
        return productService.findProductCodes(searchFilter);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Collection<Product> retrieveProduct(@RequestParam("code") List<String> codes){
        Map<String, Product> products = productService.resolveProducts(codes);

        return products.values();
    }

    @RequestMapping(value = "/_range", method = RequestMethod.GET)
    public Collection<Product> retrieveProductRange(
        @RequestParam("limit") int limit,
        @RequestParam(value = "offset", required = false, defaultValue = "0") int offset
    ){
        if(limit <= 0){
            throw new IllegalArgumentException("limit must be positive!");
        }
        if(offset < 0){
            throw new IllegalArgumentException("offset must not be negative!");
        }

        // TODO: use proper filter
        ProductSearchFilter searchFilter = ProductSearchFilter.forSearchKey(null);
        List<String> allCodes = productService.findProductCodes(searchFilter);
        int size = allCodes.size();

        if(offset > size){
            throw new IllegalArgumentException("offset must not be greater then the total amount of products (" + size + ")!");
        }

        int endPos = offset + limit;
        if(endPos > size){
            endPos = size;
        }

        List<String> matchingCodes = allCodes.subList(offset, endPos);

        Map<String, Product> products = productService.resolveProducts(matchingCodes);

        return products.values();
    }

    @RequestMapping(value = "/_details", method = RequestMethod.GET)
    public ProductDetails retrieveProductDetails(@RequestParam("code") String code){
        Product product = productService.resolveProduct(code);
        if(product == null){
            throw new IllegalArgumentException("No product found for code '" + code + "'!");
        }

        // TODO: use proper filter
        PropertySearchFilter searchFilter = PropertySearchFilter.forSearchKey(null);
        List<String> propertyCodes = productService.findProductPropertyCodes(code, searchFilter);
        if(propertyCodes == null || propertyCodes.isEmpty()){
            return new ProductDetails(product, null);
        }

        Map<String, Property<? extends PropertyType>> matchingProperties = productService.resolveProperties(code, propertyCodes);

        Map<String, PropertyValue> propertyValues = new HashMap<>();
        for (Property<? extends PropertyType> property : matchingProperties.values()) {
            propertyValues.put(property.getCode(), property.getPropertyValue());
        }

        return new ProductDetails(product, propertyValues);
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
