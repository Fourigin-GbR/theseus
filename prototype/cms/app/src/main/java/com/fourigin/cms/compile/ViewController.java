package com.fourigin.cms.compile;

import com.fourigin.cms.ContextKeys;
import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.repository.ContentRepositoryFactory;
import com.fourigin.cms.repository.ContentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/view")
public class ViewController {

    private ContentRepositoryFactory contentRepositoryFactory;

    private final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView view(
        @RequestParam("base") String base,
        @RequestParam("path") String path,
        @RequestParam(value = "flush", required = false, defaultValue = "false") boolean flushCaches){

        if (logger.isDebugEnabled()) logger.debug("Processing view request for base {} & path {}.", base, path);

        ContentResolver contentResolver = contentRepositoryFactory.getInstance(base);
        if(flushCaches) {
            contentResolver.flush();
        }

        PageInfo pageInfo = contentResolver.resolveInfo(PageInfo.class, path);

        Map<String, String> siteAttributes = contentResolver.resolveSiteAttributes();

        ContentPage contentPage = contentResolver.retrieve(pageInfo);
        if(contentPage == null){
            throw new IllegalStateException("No ContentPage assigned to PageInfo " + pageInfo);
        }

        ModelAndView modelAndView = new ModelAndView("viewPage");

        modelAndView.addObject("base", base);
        modelAndView.addObject("path", path);
        modelAndView.addObject(ContextKeys.CONTENT_PAGE, contentPage);
        modelAndView.addObject(ContextKeys.PAGE_INFO, pageInfo);
        modelAndView.addObject(ContextKeys.SITE_ATTRIBUTES, siteAttributes);

        return modelAndView;
    }

    @Autowired
    public void setContentRepositoryFactory(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }
}
