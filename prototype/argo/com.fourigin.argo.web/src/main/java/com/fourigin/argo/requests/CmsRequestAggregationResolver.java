package com.fourigin.argo.requests;

import com.fourigin.argo.InvalidParameterException;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.repository.aggregators.CmsRequestAggregation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public  class CmsRequestAggregationResolver {

    private ContentRepositoryFactory contentRepositoryFactory;

    private TemplateResolver templateResolver;

    private final Logger logger = LoggerFactory.getLogger(CmsRequestAggregationResolver.class);

    public CmsRequestAggregationResolver(ContentRepositoryFactory contentRepositoryFactory, TemplateResolver templateResolver) {
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.templateResolver = templateResolver;
    }

    @Cacheable("aggregations")
    public CmsRequestAggregation resolveAggregation(String customer, String base, String path){
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(customer, base);

        PageInfo pageInfo = contentRepository.resolveInfo(PageInfo.class, path);
        if(pageInfo == null){
            throw new IllegalStateException("No PageInfo found for path '" + path + "'! Is this path valid?");
        }

        if (logger.isDebugEnabled()) logger.debug("pageInfo for path '{}': {}", path, pageInfo);

        PageState pageState = contentRepository.resolvePageState(pageInfo);
        if(pageState == null){
            if (logger.isDebugEnabled()) logger.debug("No PageState found for page '{}'. Returning a new PageState", path);
            pageState = new PageState.Builder()
                .withStaged(false)
                .build();
        }

        TemplateReference templateReference = pageInfo.getTemplateReference();
        if(templateReference == null){
            throw new IllegalStateException("No TemplateReference defined for PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Template reference: {}", templateReference);

        String templateId = templateReference.getTemplateId();
        if(templateId == null) {
            throw new IllegalStateException("The defined TemplateReference does not include a template-id for " + pageInfo);
        }

        Template template = templateResolver.retrieve(templateId);
        if(template == null){
            throw new IllegalStateException("No template found for id '" + templateId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template: {}", templateId);

        PageInfo.ContentPageReference pageReference = pageInfo.getContentPageReference();
        String parentPath = pageReference.getParentPath();
        String contentId = pageReference.getContentId();
        if (logger.isDebugEnabled()) logger.debug("Content id: {}", contentId);

        ContentPage contentPage = contentRepository.retrieve(pageInfo);
        if(contentPage == null){
            throw new InvalidParameterException("No content found for path '" + parentPath + "' and id '" + contentId + "'!");
        }

        CmsRequestAggregation result = new CmsRequestAggregation();

        result.setContentRepository(contentRepository);
        result.setPageInfo(pageInfo);
        result.setPageState(pageState);
        result.setTemplateReference(templateReference);
        result.setTemplate(template);
        result.setContentPage(contentPage);

        return result;
    }

    @CacheEvict(value = "aggregations", allEntries = true)
    public void flush(){
        // Nothing to do here!
    }
}
