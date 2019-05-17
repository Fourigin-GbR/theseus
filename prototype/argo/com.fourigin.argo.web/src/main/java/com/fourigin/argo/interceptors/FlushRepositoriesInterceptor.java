package com.fourigin.argo.interceptors;

import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.ContentResolver;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class FlushRepositoriesInterceptor implements WebRequestInterceptor {

    private ContentRepositoryFactory contentRepositoryFactory;

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    private final Logger logger = LoggerFactory.getLogger(FlushRepositoriesInterceptor.class);

    public FlushRepositoriesInterceptor(
        ContentRepositoryFactory contentRepositoryFactory,
        CmsRequestAggregationResolver cmsRequestAggregationResolver
    ) {
        Objects.requireNonNull(contentRepositoryFactory, "contentRepositoryFactory must not bei null!");
        this.contentRepositoryFactory = contentRepositoryFactory;

        Objects.requireNonNull(cmsRequestAggregationResolver, "cmsRequestAggregationResolver must not bei null!");
        this.cmsRequestAggregationResolver = cmsRequestAggregationResolver;
    }

    @Override
    public void preHandle(WebRequest webRequest) throws IOException {
        boolean flushCaches = Boolean.valueOf(webRequest.getParameter(RequestParameters.FLUSH));

        if (!flushCaches) {
            return;
        }

        //noinspection unchecked
        Map<String, String> pathVariables = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (pathVariables.isEmpty()) {
            if (logger.isWarnEnabled()) logger.warn("Unable to flush, no path-variables available on request!");
            return;
        }

        String language = webRequest.getParameter(RequestParameters.LANGUAGE);
        if (logger.isInfoEnabled()) logger.info("language: {}, path-variables: {}", language, pathVariables);

        String project = pathVariables.get("project");

        MDC.put("project", project);
        MDC.put("language", language);

        try {
            ContentResolver contentResolver = contentRepositoryFactory.getInstance(project, language);

            if (logger.isInfoEnabled()) logger.info("Flushing content resolver for {}", language);
            contentResolver.flush();

            if (logger.isInfoEnabled()) logger.info("Flushing cmsRequestAggregationResolver", language);
            cmsRequestAggregationResolver.flush();
        } finally {
            MDC.remove("project");
            MDC.remove("language");
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) {

    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) {

    }
}
