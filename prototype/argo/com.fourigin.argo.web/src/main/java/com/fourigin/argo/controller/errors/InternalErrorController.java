package com.fourigin.argo.controller.errors;

import com.fourigin.argo.models.InvalidSiteStructureNodeException;
import com.fourigin.argo.models.InvalidSiteStructurePathException;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class InternalErrorController implements ErrorController {
    private ErrorAttributes errorAttributes;

    public InternalErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request, WebRequest webRequest) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        ModelAndView result = new ModelAndView();

        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (message != null) {
            result.addObject("message", message);
        }

        Throwable cause = null;

        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception != null) {
            result.addObject("cause", exception);

            List<Throwable> causes = new ArrayList<>();
            Throwable ex = exception;
            while (ex != null) {
                causes.add(0, ex);
                ex = ex.getCause();
            }

            result.addObject("stack", causes);

            cause = causes.get(0);
            result.addObject("cause", cause);
        }

        Map<String, Object> attributes = errorAttributes.getErrorAttributes(webRequest, false);

        result.addObject("attributeNames", attributes.keySet());
        result.addObject("attributes", attributes);

        if (status == null) {
            result.setViewName("error");
        } else {
            HttpStatus statusCode = HttpStatus.resolve((Integer) status);

            switch (statusCode) {
                case NOT_FOUND:
                    result.setViewName("error-404");
                    break;
                case INTERNAL_SERVER_ERROR:
                    if (cause != null) {
                        if (InvalidSiteStructurePathException.class.isAssignableFrom(cause.getClass())) {
                            result.setViewName("error-wrong-site-path");

                            InvalidSiteStructurePathException ex = (InvalidSiteStructurePathException) cause;
                            result.addObject("path", ex.getPath());
                            result.addObject("token", ex.getUnresolvableToken());
                        }
                        else if(InvalidSiteStructureNodeException.class.isAssignableFrom(cause.getClass())){
                            result.setViewName("error-wrong-site-node");
                        }
                    } else {
                        result.setViewName("error-500");
                    }
                    break;
                default:
                    result.setViewName("error");
                    break;
            }
        }

        return result;
    }
}
