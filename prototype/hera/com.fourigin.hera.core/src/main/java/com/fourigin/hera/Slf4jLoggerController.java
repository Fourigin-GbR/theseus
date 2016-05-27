package com.fourigin.hera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("#{environment.getProperty('hera.context-path')?:'/log'}")
@RequestMapping("/hera")
public class Slf4jLoggerController {

    private final Logger logger = LoggerFactory.getLogger(Slf4jLoggerController.class);

    @Value("${hera.application.name}")
    private String applicationName;

    @RequestMapping("config")
    @ResponseBody
    public LoggerConfiguration config(
      @RequestParam("logger") String loggerName
    ){
        if (logger.isInfoEnabled()) logger.info("Retrieving logger configuration for '{}'.", loggerName);

        String name = buildLoggerName(loggerName);

        Logger log = LoggerFactory.getLogger(name);
        LoggerConfiguration result = new LoggerConfiguration();

        result.setApplicationName(applicationName);
        result.setLoggerName(loggerName);
        result.setDebugEnabled(log.isDebugEnabled());
        result.setInfoEnabled(log.isInfoEnabled());
        result.setWarnEnabled(log.isWarnEnabled());
        result.setErrorEnabled(log.isErrorEnabled());

        if (logger.isDebugEnabled()) logger.debug("Returning {}.", result);

        return result;
    }

    @RequestMapping("debug")
    @ResponseBody
    public boolean logDebug(@RequestBody LogRequest logRequest){
        String loggerName = buildLoggerName(logRequest.getLogger());

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isDebugEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();

            if (logger.isDebugEnabled()) logger.debug("Logging (debug): {}: {}, {}", loggerName, message, args);
            log.debug(message, args);
            return true;
        }

        return false;
    }

    @RequestMapping("info")
    @ResponseBody
    public boolean logInfo(@RequestBody LogRequest logRequest){
        String loggerName = buildLoggerName(logRequest.getLogger());

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isInfoEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();

            if (logger.isDebugEnabled()) logger.debug("Logging (info): {}: {}, {}", loggerName, message, args);
            log.info(message, (Object[]) args);
            return true;
        }

        return false;
    }

    @RequestMapping("warn")
    @ResponseBody
    public boolean logWarn(@RequestBody LogRequest logRequest){
        String loggerName = buildLoggerName(logRequest.getLogger());

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isWarnEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();

            if (logger.isDebugEnabled()) logger.debug("Logging (warn): {}: {}, {}", loggerName, message, args);
            log.warn(message, (Object[]) args);
            return true;
        }

        return false;
    }

    @RequestMapping("error")
    @ResponseBody
    public boolean logError(@RequestBody LogRequest logRequest){
        String loggerName = buildLoggerName(logRequest.getLogger());

        Logger log = LoggerFactory.getLogger(loggerName);
        if(log.isErrorEnabled()){
            String message = logRequest.getMessage();
            Object[] args = logRequest.getArgs();

            if (logger.isDebugEnabled()) logger.debug("Logging (error): {}: {}, {}", loggerName, message, args);
            log.error(message, (Object[]) args);
            return true;
        }

        return false;
    }

    private String buildLoggerName(String baseName){
        return applicationName + '.' + baseName;
    }

}