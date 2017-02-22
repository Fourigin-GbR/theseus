package com.fourigin.cms.compile;

import com.fourigin.cms.compiler.PageCompilerFactory;
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

        ModelAndView modelAndView = new ModelAndView("viewPage");

        modelAndView.addObject("base", base);
        modelAndView.addObject("path", path);

        return modelAndView;
    }

    @Autowired
    public void setContentRepositoryFactory(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }
}
