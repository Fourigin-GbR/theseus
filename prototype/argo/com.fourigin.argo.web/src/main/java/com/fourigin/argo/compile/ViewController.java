package com.fourigin.argo.compile;

import com.fourigin.argo.ContextKeys;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPagePrototype;
import com.fourigin.argo.models.content.hotspots.ElementsEditorProperties;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.ContentResolver;
import com.fourigin.argo.repository.TemplateResolver;
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

    private ContentRepositoryFactory contentRepositoryFactory;

    private TemplateResolver templateResolver;

    private final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView view(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path){

        if (logger.isDebugEnabled()) logger.debug("Processing view request for base {} & path {}.", base, path);

        ContentResolver contentResolver = contentRepositoryFactory.getInstance(base);

        PageInfo pageInfo = contentResolver.resolveInfo(PageInfo.class, path);

        TemplateReference templateReference = pageInfo.getTemplateReference();
        if(templateReference == null){
            throw new IllegalStateException("No TemplateReference defined for PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Template reference: {}", templateReference);

        String templateId = templateReference.getTemplateId();
        Template template = templateResolver.retrieve(templateId);
        if(template == null){
            throw new IllegalStateException("No template found for id '" + templateId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template: {}", templateId);

        Map<String, ElementsEditorProperties> hotspots;
        ContentPagePrototype prototype = template.getPrototype();
        if(prototype == null){
            if (logger.isDebugEnabled()) logger.debug("No content prototype available for template '{}'", templateId);
            hotspots = Collections.emptyMap();
        }
        else {
            if (logger.isDebugEnabled()) logger.debug("Found content prototype for template '{}'", templateId);
            hotspots = prototype.getHotspots();
        }

        Map<String, String> siteAttributes = contentResolver.resolveSiteAttributes();

        ContentPage contentPage = contentResolver.retrieve(pageInfo);
        if(contentPage == null){
            throw new IllegalStateException("No ContentPage assigned to PageInfo " + pageInfo);
        }

        ModelAndView modelAndView = new ModelAndView("viewPage");

        modelAndView.addObject(ContextKeys.BASE, base);
        modelAndView.addObject(ContextKeys.PATH, path);
        modelAndView.addObject(ContextKeys.CONTENT_PAGE, contentPage);
        modelAndView.addObject(ContextKeys.PAGE_INFO, pageInfo);
        modelAndView.addObject(ContextKeys.SITE_ATTRIBUTES, siteAttributes);
        modelAndView.addObject(ContextKeys.HOTSPOTS, hotspots);

        return modelAndView;
    }

    @Autowired
    public void setContentRepositoryFactory(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @Autowired
    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }
}
