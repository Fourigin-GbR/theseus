package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.TextContentElement;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampDataSourceResponse implements DataSourceResponse {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private Date creationDate;

    private TextContentElement content;

    public TimestampDataSourceResponse(){
        creationDate = new Date();

        String timestamp = FORMAT.format(creationDate);

        content = new TextContentElement.Builder()
            .withName("processingTimestamp")
            .withContent(timestamp)
            .build();
    }

    @Override
    public String getChecksum() {
        return ChecksumGenerator.getChecksum(content);
    }

    @Override
    public ContentElement getContent() {
        return content;
    }
}
