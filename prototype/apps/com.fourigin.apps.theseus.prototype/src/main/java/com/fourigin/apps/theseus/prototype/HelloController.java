//package com.fourigin.apps.theseus.prototype;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpServletRequest;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//@RestController
//public class HelloController {
//
//    @Autowired
//    private MessageSource messageSource;
//
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
//    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//
//    @RequestMapping("/")
//    public String index(HttpServletRequest request) {
//        String builder = "Request details:\n" +
//          " - requestURL: '" + request.getRequestURL() + "'\n" +
//          " - protocol: '" + request.getProtocol() + "'\n" +
//          " - serverName: '" + request.getServerName() + "'\n" +
//          " - contextPath: '" + request.getContextPath() + "'\n" +
//          " - method: '" + request.getMethod() + "'\n" +
//          " - pathInfo: '" + request.getPathInfo() + "'\n" +
//          " - queryString: '" + request.getQueryString() + "'\n" +
//          " - requestURI: '" + request.getRequestURI() + "'\n" +
//          " - servletPath: '" + request.getServletPath() + "'\n" +
//          " - remoteUser: '" + request.getRemoteUser() + "'\n" +
//          " - port (local/remote/server): '" + request.getLocalPort() + "/" + request.getRemotePort() + "/" + request.getServerPort() + "'\n";
//
//        System.out.println(builder);
//
//        Locale locale = LocaleContextHolder.getLocale();
//
//        String date = dateFormat.format(new Date());
//        String time = timeFormat.format(new Date());
//
//        return messageSource.getMessage("greeting", new Object[]{date, time}, locale);
//    }
//}