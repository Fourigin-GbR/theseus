package com.fourigin.theseus;

import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.repository.filter.ProductSearchFilter;
import com.fourigin.theseus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductsController {

    private ProductService productService;

    @RequestMapping(value = "/_codes", method = RequestMethod.GET)
    public List<String> retrieveCodes(){
        return productService.findProductCodes(ProductSearchFilter.forSearchKey(null));
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

        List<String> allCodes = productService.findProductCodes(ProductSearchFilter.forSearchKey(null));
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

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}