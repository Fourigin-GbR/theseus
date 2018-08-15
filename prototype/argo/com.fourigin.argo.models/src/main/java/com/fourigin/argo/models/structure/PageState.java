package com.fourigin.argo.models.structure;

import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageMetaData;
import com.fourigin.argo.models.content.DataSourceContent;
import com.fourigin.argo.models.content.elements.ContentElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

public class PageState {
    private boolean staged;
    private CompileState compileState;
    private ContentPageChecksum checksum;
    private String revision;

    private final Logger logger = LoggerFactory.getLogger(PageState.class);

    public boolean isStaged() {
        return staged;
    }

    public void setStaged(boolean staged) {
        this.staged = staged;
    }

    public CompileState getCompileState() {
        return compileState;
    }

    public void setCompileState(CompileState compileState) {
        this.compileState = compileState;
    }

    public ContentPageChecksum getChecksum() {
        return checksum;
    }

    public void setChecksum(String metaDataValue, String contentValue, SortedMap<String, String> dataSourceValues) {
        this.checksum = new ContentPageChecksum(metaDataValue, contentValue, dataSourceValues);
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public void buildChecksum(ContentPage contentPage) {
        ContentPageMetaData metaData = contentPage.getMetaData();
        List<ContentElement> content = contentPage.getContent();
        Collection<DataSourceContent> dataSources = contentPage.getDataSourceContents();

        String metaDataValue = ChecksumGenerator.getChecksum(metaData);
        String contentValue = ChecksumGenerator.getChecksum(content);

        Map<String, String> dataSourceValues = new HashMap<>();
        if (dataSources != null) {
            for (DataSourceContent dataSource : dataSources) {
                String name = dataSource.getName();
                ContentElement dataSourceContent = dataSource.getContent();
                String dataSourceChecksum = null;
                if(dataSourceContent != null) {
                    dataSourceChecksum = ChecksumGenerator.getChecksum(dataSourceContent);
                }

                if (logger.isDebugEnabled())
                    logger.debug("Put '{}': '{}' data source checksum value", name, dataSourceChecksum);
                dataSourceValues.put(name, dataSourceChecksum);
            }
        }

        this.checksum = new ContentPageChecksum(metaDataValue, contentValue, dataSourceValues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageState)) return false;
        PageState pageState = (PageState) o;
        return staged == pageState.staged &&
            Objects.equals(compileState, pageState.compileState) &&
            Objects.equals(checksum, pageState.checksum) &&
            Objects.equals(revision, pageState.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staged, compileState, checksum, revision);
    }

    @Override
    public String toString() {
        return "PageState{" +
            "staged=" + staged +
            ", compileState=" + compileState +
            ", checksum=" + checksum +
            ", revision='" + revision + '\'' +
            '}';
    }
}
