package com.fourigin.argo.controller.search;

import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.argo.controller.search.request.SearchRequest;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.datasource.index.DataSourceIndexProcessing;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/{project}/search")
public class SearchController {
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private ContentRepositoryFactory contentRepositoryFactory;

    public SearchController(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }
    
    @RequestMapping("/")
    @ResponseBody
    public List<String> resolveMatchingIndexTargets(
        @PathVariable String project,
        @RequestParam(RequestParameters.LANGUAGE) String language,
        @RequestParam(RequestParameters.SITE_PATH) String path,
        @RequestBody SearchRequest request
    ) {
        if (logger.isDebugEnabled()) logger.debug("Resolving matching index for {}/{} and {}", language, path, request);

        MDC.put("project", project);
        MDC.put("language", language);

        try {
            ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);

            PageInfo info = contentRepository.resolveInfo(PageInfo.class, path);
            if (logger.isDebugEnabled()) logger.debug("Resolved info: {}", info);

            String indexName = request.getIndex();
            DataSourceIndex index = contentRepository.resolveIndex(info, indexName);
            if (logger.isDebugEnabled()) logger.debug("Resolved index: {}", index);

            List<String> references = DataSourceIndexProcessing.resolveMatchingIndexTargets(
                    index,
                    request.getCategories(),
                    request.getFields(),
                    language
            );
            if (logger.isDebugEnabled()) logger.debug("References: {}", references);

//            List<String> references = index.getReferences();
//            if (logger.isDebugEnabled()) logger.debug("References: {}", references);
//
//            Map<String, Boolean> matchingFlags = new HashMap<>();
//            for (String reference : references) {
//                // initialization with 'null'
//                matchingFlags.put(reference, null);
//            }
//
//            // categories
//            Map<String, Set<String>> filterCategories = request.getCategories();
//            if (filterCategories != null && !filterCategories.isEmpty()) {
//                if (logger.isDebugEnabled()) logger.debug("Filtering by categories ...");
//                for (Map.Entry<String, Set<String>> entry : filterCategories.entrySet()) {
//                    String categoryName = entry.getKey();
//                    if (logger.isDebugEnabled()) logger.debug("Verifying category '{}'", categoryName);
//
//                    Map<String, List<Integer>> matchingCategory = index.getCategories().get(categoryName);
//                    if (logger.isDebugEnabled()) logger.debug("Corresponding index entry: {}", matchingCategory);
//
//                    if (matchingCategory == null) {
//                        if (logger.isDebugEnabled())
//                            logger.debug("No indexed category found for name '{}'", categoryName);
//                        // TODO: return an empty list?
//                        continue;
//                    }
//
//                    Set<String> categoryValues = entry.getContent();
//                    if (logger.isDebugEnabled()) logger.debug("Requested category values: {}", categoryValues);
//
//                    for (String categoryValue : categoryValues) {
//                        if (logger.isDebugEnabled()) logger.debug("Verifying category value '{}'", categoryValue);
//                        List<Integer> matchingReferenceNumbers = matchingCategory.get(categoryValue);
//                        if (matchingReferenceNumbers != null && !matchingReferenceNumbers.isEmpty()) {
//                            if (logger.isDebugEnabled()) logger.debug("Found matches: {}", matchingReferenceNumbers);
//                            for (Integer referenceNumber : matchingReferenceNumbers) {
//                                flagMatchingReference(referenceNumber, references, matchingFlags, "category: " + categoryName + ":" + categoryValue);
////                            String reference = references.get(referenceNumber);
////                            Boolean previousFlag = matchingFlags.get(reference);
////                            if(previousFlag == null || !previousFlag) {
////                                if (logger.isDebugEnabled()) logger.debug("Reference '{}' is matching the category '{}:{}'", reference, categoryName, categoryValue);
////                                matchingFlags.put(reference, true);
////                            }
//                            }
//                        } else {
//                            if (logger.isDebugEnabled())
//                                logger.debug("No matches found for category value '{}'", categoryValue);
//                        }
//                    }
//                }
//            }
//
//            // fields
//            Map<String, FieldValueComparator> fieldComparators = request.getFields();
//            if (fieldComparators != null && !fieldComparators.isEmpty()) {
//                if (logger.isDebugEnabled()) logger.debug("Filtering by field comparators ...");
//
//                List<FieldValue> fields = index.getFields();
//                Map<String, FieldValue> mappedFields = new HashMap<>();
//                for (FieldValue field : fields) {
//                    mappedFields.put(field.getName(), field);
//                }
//
//                for (Map.Entry<String, FieldValueComparator> entry : fieldComparators.entrySet()) {
//                    String fieldName = entry.getKey();
//                    FieldValueComparator fieldComparator = entry.getContent();
//                    if (logger.isDebugEnabled())
//                        logger.debug("Verifying comparator for field '{}': {}", fieldName, fieldComparator);
//
//                    FieldValue fieldValue = mappedFields.get(fieldName);
//                    if (logger.isDebugEnabled()) logger.debug("Comparing with {}", fieldValue);
//
//                    int referenceNumber = 0;
//                    for (String value : fieldValue.getContent()) {
//                        if (isMatching(fieldComparator, fieldValue.getType(), value)) {
//                            flagMatchingReference(referenceNumber, references, matchingFlags, "field: " + fieldName + ":" + value);
//                        } else {
//                            flagNotMatchingReference(referenceNumber, references, matchingFlags, "field: " + fieldName + ":" + value);
//                        }
//
//                        referenceNumber++;
//                    }
//                }
//            }
//
//            // filtering the result
//            if (logger.isDebugEnabled()) logger.debug("Filtering references with matching flags: {}", matchingFlags);
//            Iterator<String> iter = references.iterator();
//            while (iter.hasNext()) {
//                String reference = iter.next();
//                Boolean flag = matchingFlags.get(reference);
//                if (flag == null || !flag) {
//                    iter.remove();
//                }
//            }

            return references;
        }
        finally {
            MDC.remove("project");
            MDC.remove("language");
        }
    }
}
