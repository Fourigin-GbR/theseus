package com.fourigin.argo.search;

import com.fourigin.argo.compile.RequestParameters;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private ContentRepositoryFactory contentRepositoryFactory;

    public SearchController(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String simpleRequest(){
        return "I'm here!";
    }

    @RequestMapping("/")
    @ResponseBody
    public List<String> resolveMatchingIndexTargets(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path,
        @RequestBody SearchRequest request
    ) {
        if (logger.isDebugEnabled()) logger.debug("Resolving matching index for {}/{} and {}", base, path, request);

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(base);

        PageInfo info = contentRepository.resolveInfo(PageInfo.class, path);
        if (logger.isDebugEnabled()) logger.debug("Resolved info: {}", info);

        String indexName = request.getIndex();
        DataSourceIndex index = contentRepository.resolveIndex(info, indexName);
        if (logger.isDebugEnabled()) logger.debug("Resolved index: {}", index);

        List<String> references = index.getReferences();
        if (logger.isDebugEnabled()) logger.debug("References: {}", references);

        Map<String, Boolean> matchingFlags = new HashMap<>();
        for (String reference : references) {
            // initialization with 'null'
            matchingFlags.put(reference, null);
        }

        Map<String, Set<String>> filterCategories = request.getCategories();
        if(filterCategories != null && !filterCategories.isEmpty()){
            if (logger.isDebugEnabled()) logger.debug("Filtering by categories ...");
            for (Map.Entry<String, Set<String>> entry : filterCategories.entrySet()) {
                String categoryName = entry.getKey();
                if (logger.isDebugEnabled()) logger.debug("Verifying category '{}'", categoryName);

                Map<String, List<Integer>> matchingCategory = index.getCategories().get(categoryName);
                if (logger.isDebugEnabled()) logger.debug("Corresponding index entry: {}", matchingCategory);

                if(matchingCategory == null){
                    if (logger.isDebugEnabled()) logger.debug("No indexed category found for name '{}'", categoryName);
                    // TODO: return an empty list?
                    continue;
                }

                Set<String> categoryValues = entry.getValue();
                if (logger.isDebugEnabled()) logger.debug("Requested category values: {}", categoryValues);

                for (String categoryValue : categoryValues) {
                    if (logger.isDebugEnabled()) logger.debug("Verifying category value '{}'", categoryValue);
                    List<Integer> matchingReferenceNumbers = matchingCategory.get(categoryValue);
                    if(matchingReferenceNumbers != null && !matchingReferenceNumbers.isEmpty()) {
                        if (logger.isDebugEnabled()) logger.debug("Found matches: {}", matchingReferenceNumbers);
                        for (Integer referenceNumber : matchingReferenceNumbers) {
                            String reference = references.get(referenceNumber);
                            Boolean previousFlag = matchingFlags.get(reference);
                            if(previousFlag == null || !previousFlag) {
                                if (logger.isDebugEnabled()) logger.debug("Reference '{}' is matching the category '{}:{}'", reference, categoryName, categoryValue);
                                matchingFlags.put(reference, true);
                            }
                        }
                    }
                    else {
                        if (logger.isDebugEnabled()) logger.debug("No matches found for category value '{}'", categoryValue);
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) logger.debug("Filtering references with matching flags: {}", matchingFlags);
        Iterator<String> iter = references.iterator();
        while(iter.hasNext()){
            String reference = iter.next();
            Boolean flag = matchingFlags.get(reference);
            if(flag == null || !flag) {
                iter.remove();
            }
        }

        return references;
    }
}
