package com.fourigin.argo.interceptors;

import com.fourigin.argo.compile.RequestParameters;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.ContentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import java.io.IOException;
import java.util.Objects;

public class FlushRepositoriesInterceptor implements WebRequestInterceptor {

    private ContentRepositoryFactory contentRepositoryFactory;

    private final Logger logger = LoggerFactory.getLogger(FlushRepositoriesInterceptor.class);

    public FlushRepositoriesInterceptor(ContentRepositoryFactory contentRepositoryFactory) {
        Objects.requireNonNull(contentRepositoryFactory, "contentRepositoryFactory must not bei null!");

        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @Override
    public void preHandle(WebRequest request) throws IOException {
        boolean flushCaches = Boolean.valueOf(request.getParameter(RequestParameters.FLUSH));

        if(flushCaches) {
            String base = request.getParameter(RequestParameters.BASE);
            ContentResolver contentResolver = contentRepositoryFactory.getInstance(base);

            if (logger.isInfoEnabled()) logger.info("Flushing content resolver for {}", base);
            contentResolver.flush();
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) {
        // nothing to do
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) {
        // nothing to do
    }
}
