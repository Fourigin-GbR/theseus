package com.fourigin.argo.compile;

import com.fourigin.argo.ContextKeys;
import com.fourigin.argo.models.content.ContentPagePrototype;
import com.fourigin.argo.models.content.hotspots.ElementsEditorProperties;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.repository.ContentResolver;
import com.fourigin.argo.repository.aggregators.CmsRequestAggregation;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/view")
public class ViewController {

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    private final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView view(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path){

        if (logger.isDebugEnabled()) logger.debug("Processing view request for base {} & path {}.", base, path);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(base, path);

//        ContentResolver contentResolver = contentRepositoryFactory.getInstance(base);
//
//        PageInfo pageInfo = contentResolver.resolveInfo(PageInfo.class, path);
//
//        TemplateReference templateReference = pageInfo.getTemplateReference();
//        if(templateReference == null){
//            throw new IllegalStateException("No TemplateReference defined for PageInfo " + pageInfo);
//        }
//        if (logger.isDebugEnabled()) logger.debug("Template reference: {}", templateReference);
//
//        String templateId = templateReference.getTemplateId();
//        Template template = templateResolver.retrieve(templateId);
//        if(template == null){
//            throw new IllegalStateException("No template found for id '" + templateId + "'!");
//        }
//        if (logger.isDebugEnabled()) logger.debug("Template: {}", templateId);

        Template template = aggregation.getTemplate();

        Map<String, ElementsEditorProperties> hotspots;
        ContentPagePrototype prototype = template.getPrototype();
        if(prototype == null){
            if (logger.isDebugEnabled()) logger.debug("No content prototype available for template '{}'", template.getId());
            hotspots = Collections.emptyMap();
        }
        else {
            if (logger.isDebugEnabled()) logger.debug("Found content prototype for template '{}'", template.getId());
            hotspots = prototype.getHotspots();
        }

        ContentResolver contentResolver = aggregation.getContentResolver();

        Map<String, String> siteAttributes = contentResolver.resolveSiteAttributes();

//        ContentPage contentPage = contentResolver.retrieve(pageInfo);
//        if(contentPage == null){
//            throw new IllegalStateException("No ContentPage assigned to PageInfo " + pageInfo);
//        }

        ModelAndView modelAndView = new ModelAndView("viewPage");

        modelAndView.addObject(ContextKeys.BASE, base);
        modelAndView.addObject(ContextKeys.PATH, path);
        modelAndView.addObject(ContextKeys.CONTENT_PAGE, aggregation.getContentPage());
        modelAndView.addObject(ContextKeys.PAGE_INFO, aggregation.getPageInfo());
        modelAndView.addObject(ContextKeys.SITE_ATTRIBUTES, siteAttributes);
        modelAndView.addObject(ContextKeys.HOTSPOTS, hotspots);

        return modelAndView;
    }

    @Autowired
    public void setCmsRequestAggregationResolver(CmsRequestAggregationResolver cmsRequestAggregationResolver) {
        this.cmsRequestAggregationResolver = cmsRequestAggregationResolver;
    }
}
