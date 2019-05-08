package com.fourigin.argo.controller.errors;

import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.models.content.UnresolvableContentPathException;
import com.fourigin.argo.repository.UnresolvableSiteStructurePathException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnresolvableContentPathException.class)
    protected ResponseEntity<ServiceErrorResponse> handleUnresolvableContentPath(UnresolvableContentPathException ex) {
        return new ResponseEntity<>(
            new ServiceErrorResponse(
                500,
                "Unresolvable content path",
                "Content path '" + ex.getPath() + "' can not be resolved!",
                ex
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(UnresolvableSiteStructurePathException.class)
    protected ResponseEntity<ServiceErrorResponse> handleSiteStructurePath(UnresolvableSiteStructurePathException ex) {
        return new ResponseEntity<>(
            new ServiceErrorResponse(
                500,
                "Unresolvable site structure path",
                "The token '" + ex.getUnresolvableToken() + "' in site structure path '" + ex.getPath() + "' can not be resolved!",
                ex
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}


//@Controller
//public class ErrorMessagesController implements ErrorController {
//    @Override
//    public String getErrorPath() {
//        return "/error";
//    }
//
//    @RequestMapping("/error")
//    public String handleError(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        if (status == null) {
//            return "error-general";
//        }
//
//        HttpStatus statusCode = HttpStatus.valueOf(status.toString());
//
//        switch (statusCode){
//            case NOT_FOUND:
//                return "error-404";
//            case INTERNAL_SERVER_ERROR:
//                return "error-500";
//            default:
//                return "error-general";
//        }
//    }
//}
