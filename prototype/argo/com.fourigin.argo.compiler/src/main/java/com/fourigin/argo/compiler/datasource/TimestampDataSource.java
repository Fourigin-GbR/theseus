package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.repository.ContentResolver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class TimestampDataSource implements DataSource<EmptyDataSourceQuery> {
    public static final String TYPE = "TIMESTAMP";

    private static final SimpleDateFormat FORMAT;

    static {
        FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ZZZ", Locale.US);
        FORMAT.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ContentElement generateContent(ContentResolver contentResolver, DataSourceIdentifier id, EmptyDataSourceQuery query) {
        String timestamp = FORMAT.format(new Date());

        return new TextContentElement.Builder()
            .withName("processedOn")
            .withContent(timestamp)
            .build();
    }
}
