//package com.fourigin.argo.forms;
//
//import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.util.ClassUtils;
//
//public class AnnotatedFormsEntryProcessor implements FormsEntryProcessor {
//    private Object processor;
//    private Class processorClass;
//    private String name;
//
//    public AnnotatedFormsEntryProcessor(Object processor) {
//        this.processor = processor;
//
//        this.processorClass = ClassUtils.getUserClass(processor);
//
//        EntryProcessor entryProcessor = AnnotationUtils.findAnnotation(processorClass, EntryProcessor.class);
//        if (entryProcessor == null) {
//            throw new IllegalArgumentException("Missing EntryProcessor annotation in class '" + processorClass.getName() + "'!");
//        }
//
//        this.name = entryProcessor.value();
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public ProcessingHistoryRecord processEntry(String entryId, FormsRegistry registry) {
//        return null;
//    }
//}
