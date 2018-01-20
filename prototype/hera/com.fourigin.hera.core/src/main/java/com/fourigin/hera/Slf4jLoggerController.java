package com.fourigin.hera;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

@Controller("heraController")
public class Slf4jLoggerController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(Slf4jLoggerController.class);

    @Value("${hera.application.name}")
    private String applicationName;

    @Value("${hera.console.logging.enabled}")
    private boolean enableConsoleLogging;

    @Value("${hera.server.logging.enabled}")
    private boolean enableServerLogging;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        System.out.println("Request...");

        String servletPath = request.getServletPath();
//        String queryString = request.getQueryString();
//        String contextPath = request.getContextPath();
//        String requestUri = request.getRequestURI();
//        String pathInfo = request.getPathInfo();
//        System.out.println(" servletPath: '" + servletPath + "'\n queryString: '" + queryString + "'");

        ModelAndView result = new ModelAndView();
        result.setView(new MappingJackson2JsonView());

        if(servletPath.endsWith("/config")){
            String loggerName = request.getParameter("logger");
            result.addObject(config(loggerName));
            return result;
        }

        BufferedReader reader = request.getReader();
        LogRequest logRequest = objectMapper.readValue(reader, LogRequest.class);

        if(servletPath.endsWith("/trace")){
            logTrace(logRequest);
        }
        else if(servletPath.endsWith("/debug")){
            logDebug(logRequest);
        }
        else if(servletPath.endsWith("/info")){
            logInfo(logRequest);
        }
        else if(servletPath.endsWith("/warn")){
            logWarn(logRequest);
        }
        else if(servletPath.endsWith("/error")){
            logError(logRequest);
        }
        else {
            System.out.println("Unknown request...");
        }

        return result;
    }

    private LoggerConfiguration config(String loggerName){
        if (logger.isInfoEnabled()) logger.info("Retrieving logger configuration for '{}'.", loggerName);

        Logger log = LoggerFactory.getLogger(loggerName);
        LoggerConfiguration result = new LoggerConfiguration();

        result.setApplicationName(applicationName);
        result.setLoggerName(loggerName);
        result.setTraceEnabled(log.isTraceEnabled());
        result.setDebugEnabled(log.isDebugEnabled());
        result.setInfoEnabled(log.isInfoEnabled());
        result.setWarnEnabled(log.isWarnEnabled());
        result.setErrorEnabled(log.isErrorEnabled());

        result.setConsoleLoggingEnabled(enableConsoleLogging);
        result.setServerLoggingEnabled(enableServerLogging);

        if (logger.isDebugEnabled()) logger.debug("Returning {}.", result);

        return result;
    }

    private void logTrace(LogRequest logRequest){
        String loggerName = logRequest.getLogger();

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isTraceEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();
            if (logger.isDebugEnabled()) logger.debug("Logging (trace): {}: {}, {}", loggerName, message, args);
            log.trace(message, args);
        }
    }

    private void logDebug(LogRequest logRequest){
        String loggerName = logRequest.getLogger();

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isDebugEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();
            if (logger.isDebugEnabled()) logger.debug("Logging (debug): {}: {}, {}", loggerName, message, args);
            log.debug(message, args);
        }
    }

    private void logInfo(LogRequest logRequest){
        String loggerName = logRequest.getLogger();

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isInfoEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();
            if (logger.isDebugEnabled()) logger.debug("Logging (info): {}: {}, {}", loggerName, message, args);
            log.info(message, (Object[]) args);
        }
    }

    private void logWarn(LogRequest logRequest){
        String loggerName = logRequest.getLogger();

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isWarnEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();
            if (logger.isDebugEnabled()) logger.debug("Logging (warn): {}: {}, {}", loggerName, message, args);
            log.warn(message, (Object[]) args);
        }
    }

    private void logError(LogRequest logRequest){
        String loggerName = logRequest.getLogger();

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isErrorEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();
            if (logger.isDebugEnabled()) logger.debug("Logging (error): {}: {}, {}", loggerName, message, args);
            log.error(message, (Object[]) args);
        }
    }
}
