/*
hera.core (META-INF)
 */

var urlConfig = '/config';
var urlDebug = '/debug';
var urlInfo = '/info';
var urlWarn = '/warn';
var urlError = '/error';

var LoggerFactory = (function() {
    var heraContextPath;

    var LoggerFactory = function (heraContext) {
        heraContextPath = heraContext;
        return this;
    };

    LoggerFactory.prototype.getLogger = function(loggerName){
        return new Logger(heraContextPath, loggerName);
    };

    return LoggerFactory;
})();

var Logger = (function(){
    var name;
    var heraContextPath;
    var loggerConfig;

    var Logger = function(heraContext, loggerName){
        name = loggerName;
        heraContextPath = heraContext;

        $.ajax({
            url: heraContextPath + urlConfig,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            data: "logger=" + name,
            dataType: 'json',
            async: true,
            success: function ( data ) {
                console.log("Logger configuration: " + JSON.stringify(data));
                loggerConfig = data.loggerConfiguration;
            }
        });

        return this;
    };

    Logger.prototype.debug = function(message, args){
        if(!loggerConfig){
            console.log("Uninitialized logger config!");
            return;
        }

        if(loggerConfig.debugEnabled){
            if(loggerConfig.consoleLoggingEnabled){
                this.logToConsole('debug', message, args);
            }
            this.sendToServer(urlDebug, message, args);
        }
        else {
            if(loggerConfig.consoleLoggingEnabled) {
                console.log("Skipping debug log ...");
            }
        }
    };

    Logger.prototype.info = function(message, args){
        if(!loggerConfig){
            console.log("Uninitialized logger config!");
            return;
        }

        if(loggerConfig.infoEnabled){
            if(loggerConfig.consoleLoggingEnabled){
                this.logToConsole('info', message, args);
            }
            this.sendToServer(urlInfo, message, args);
        }
        else {
            if(loggerConfig.consoleLoggingEnabled) {
                console.log("Skipping info log ...");
            }
        }
    };

    Logger.prototype.warn = function(message, args){
        if(!loggerConfig){
            console.log("Uninitialized logger config!");
            return;
        }

        if(loggerConfig.warnEnabled){
            if(loggerConfig.consoleLoggingEnabled){
                this.logToConsole('warn', message, args);
            }
            this.sendToServer(urlWarn, message, args);
        }
        else {
            if(loggerConfig.consoleLoggingEnabled) {
                console.log("Skipping warn log ...");
            }
        }
    };

    Logger.prototype.error = function(message, args){
        if(!loggerConfig){
            console.log("Uninitialized logger config!");
            return;
        }

        if(loggerConfig.errorEnabled){
            if(loggerConfig.consoleLoggingEnabled){
                this.logToConsole('error', message, args);
            }
            this.sendToServer(urlError, message, args);
        }
        else {
            if(loggerConfig.consoleLoggingEnabled) {
                console.log("Skipping error log ...");
            }
        }
    };

    Logger.prototype.sendToServer = function(levelUrl, message, args){
        var data = {
            logger: name,
            message: message,
            args: args
        };

        $.ajax({
            url: heraContextPath + levelUrl,
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            dataType: 'json',
            async: true,
            success: function (msg) {
            }
        });
    };

    Logger.prototype.logToConsole = function(level, message, args){
        if(level === 'debug'){
            console.log(message + ' - ' + args);
        }
        else if(level === 'info'){
            console.info(message + ' - ' + args);
        }
        else if(level === 'warn'){
            console.warn(message + ' - ' + args);
        }
        else if(level === 'error'){
            console.error(message + ' - ' + args);
        }
        else {
            console.error("Unknown log level '" + level + "'! " + message + args);
        }
    };

    return Logger;
})();

