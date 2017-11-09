//package com.fourigin.apps.theseus.prototype;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
//
//import java.io.File;
//
////@Component
//public class ServletContainerCustomizer implements EmbeddedServletContainerCustomizer {
//
//    @Value("${server.document-root}")
//    private String documentRoot;
//
//    private final Logger logger = LoggerFactory.getLogger(ServletContainerCustomizer.class);
//
//    @Override
//    public void customize(ConfigurableEmbeddedServletContainer container) {
//        if(documentRoot != null) {
//            if (logger.isInfoEnabled()) logger.info("Found specified document-root property '{}'.", documentRoot);
//
//            File docRoot = new File(documentRoot);
//            if(!docRoot.exists()){
//                if (logger.isErrorEnabled()) logger.error("Specified document-root property contains a path to a not existing directory '{}'.", docRoot.getAbsolutePath());
//            }
//            else {
//                if (logger.isInfoEnabled()) logger.info("Modifying document root to '{}'.", docRoot.getAbsolutePath());
//                container.setDocumentRoot(new File(documentRoot));
//            }
//        }
//    }
//}
//
