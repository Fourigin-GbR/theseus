package com.fourigin.hera.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @Value("${hera.context-path}")
    private String contextPath;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @ModelAttribute("heraContextPath")
    public String heraContextPath(){
        return contextPath;
    }
}
