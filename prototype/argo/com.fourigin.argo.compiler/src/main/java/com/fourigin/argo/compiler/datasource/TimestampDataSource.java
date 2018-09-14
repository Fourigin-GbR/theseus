package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.datasource.DataSource;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.structure.nodes.PageInfo;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    public List<ContentElement> generateContent(PageInfo ownerPage, DataSourceIdentifier id, EmptyDataSourceQuery query, Map<String, Object> context) {
        String timestamp = FORMAT.format(new Date());

        return Collections.singletonList(new TextContentElement.Builder()
            .withName("processedOn")
            .withContent(timestamp)
            .build());
    }
}
