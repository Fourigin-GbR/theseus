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
import java.util.TreeMap;

public class PageState {
    private boolean staged;
    private boolean live;
    private long timestampLiveSwitch;
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

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public long getTimestampLiveSwitch() {
        return timestampLiveSwitch;
    }

    public void setTimestampLiveSwitch(long timestampLiveSwitch) {
        this.timestampLiveSwitch = timestampLiveSwitch;
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
        if (logger.isDebugEnabled()) logger.debug("meta-data-checksum: {}", metaDataValue);

        String contentValue = ChecksumGenerator.getChecksum(content);
        if (logger.isDebugEnabled()) logger.debug("content-checksum: {}", contentValue);

        Map<String, String> dataSourceValues = new HashMap<>();
        if (dataSources != null) {
            for (DataSourceContent dataSource : dataSources) {
                String name = dataSource.getName();
                if (dataSource.isTransientContent()) {
                    if (logger.isDebugEnabled()) logger.debug("Ignoring data source '{}' because the content is transient", name);
                    continue;
                }

                List<ContentElement> dataSourceContent = dataSource.getContent();
                if (dataSourceContent == null) {
                    if (logger.isDebugEnabled()) logger.debug("Ignoring data source '{}' because the content is not present", name);
                    continue;
                }

                String dataSourceChecksum = ChecksumGenerator.getChecksum(dataSourceContent);
                dataSourceValues.put(name, dataSourceChecksum);
                if (logger.isDebugEnabled())
                    logger.debug("data source checksum of '{}': '{}'", name, dataSourceChecksum);
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
            live == pageState.live &&
            timestampLiveSwitch == pageState.timestampLiveSwitch &&
            Objects.equals(compileState, pageState.compileState) &&
            Objects.equals(checksum, pageState.checksum) &&
            Objects.equals(revision, pageState.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staged, live, timestampLiveSwitch, compileState, checksum, revision);
    }

    @Override
    public String toString() {
        return "PageState{" +
            "staged=" + staged +
            ", live=" + live +
            ", timestampLiveSwitch=" + timestampLiveSwitch +
            ", compileState=" + compileState +
            ", checksum=" + checksum +
            ", revision='" + revision + '\'' +
            '}';
    }

    public static class Builder {
        private boolean staged;
        private CompileState compileState;
        private String metaDataChecksum;
        private String contentChecksum;
        private SortedMap<String, String> dataSourceChecksum;
        private String revision;

        public Builder withStaged(boolean staged) {
            this.staged = staged;
            return this;
        }

        public Builder withCompileState(CompileState compileState) {
            this.compileState = compileState;
            return this;
        }

        public Builder withMetaDataChecksum(String metaDataChecksum) {
            this.metaDataChecksum = metaDataChecksum;
            return this;
        }

        public Builder withContentChecksum(String contentChecksum) {
            this.contentChecksum = contentChecksum;
            return this;
        }

        public Builder withDataSourceChecksum(SortedMap<String, String> dataSourceChecksum) {
            this.dataSourceChecksum = new TreeMap<>(dataSourceChecksum);
            return this;
        }

        public Builder withRevision(String revision) {
            this.revision = revision;
            return this;
        }

        public PageState build() {
            PageState state = new PageState();

            state.setStaged(staged);
            state.setLive(false);

            if (compileState != null) {
                state.setCompileState(compileState);
            }

            String meta = metaDataChecksum == null ? "" : metaDataChecksum;
            String content = contentChecksum == null ? "" : contentChecksum;
            SortedMap<String, String> dataSource = dataSourceChecksum == null ? new TreeMap<>() : dataSourceChecksum;
            state.setChecksum(meta, content, dataSource);

            String rev = revision == null ? "1" : revision;
            state.setRevision(rev);

            return state;
        }
    }
}
