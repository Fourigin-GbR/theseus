package com.fourigin.argo.controller.compile;

import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.argo.models.content.ContentPagePrototype;
import com.fourigin.argo.models.content.hotspots.ElementsEditorProperties;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.repository.ContentResolver;
import com.fourigin.argo.repository.aggregators.CmsRequestAggregation;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import com.fourigin.argo.template.engine.api.Argo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/{project}/view")
public class ViewController {

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    private final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView view(
        @PathVariable String project,
        @RequestParam(RequestParameters.LANGUAGE) String language,
        @RequestParam(RequestParameters.SITE_PATH) String path
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing view request for language {} & path {}.", language, path);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(project, language, path);

        Template template = aggregation.getTemplate();

        Map<String, ElementsEditorProperties> hotspots;
        ContentPagePrototype prototype = template.getPrototype();
        if (prototype == null) {
            if (logger.isDebugEnabled())
                logger.debug("No content prototype available for template '{}'", template.getId());
            hotspots = Collections.emptyMap();
        } else {
            if (logger.isDebugEnabled()) logger.debug("Found content prototype for template '{}'", template.getId());
            hotspots = prototype.getHotspots();
        }

        ContentResolver contentResolver = aggregation.getContentResolver();

        // create result
        ModelAndView modelAndView = new ModelAndView("viewPage");

        modelAndView.addObject("data_hotspots", hotspots);

        modelAndView.addObject("argo", new Argo.Builder()
            .withProject(project)
            .withLanguage(language)
            .withPath(path)
            .withContentPage(aggregation.getContentPage())
            .withPageInfo(aggregation.getPageInfo())
            .withSiteAttributes(contentResolver.resolveSiteAttributes())
            .build()
        );

        return modelAndView;
    }

    @Autowired
    public void setCmsRequestAggregationResolver(CmsRequestAggregationResolver cmsRequestAggregationResolver) {
        this.cmsRequestAggregationResolver = cmsRequestAggregationResolver;
    }
}
