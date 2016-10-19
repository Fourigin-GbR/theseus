package com.fourigin.cms.models.structure.nodes;

import com.fourigin.cms.models.datasource.DataSourceIdentifier;
import com.fourigin.cms.models.structure.CompileState;

import java.util.SortedMap;

public class SitePage extends AbstractSiteNode implements SiteNode {
    private boolean staged;
    private CompileState compileState;
    private Checksum checksum;

    public ContentPageReference getContentPageReference(){
        return new ContentPageReference(getPath(), getName());
    }

    public CompileState getCompileState() {
        return compileState;
    }

    public void setCompileState(CompileState compileState) {
        this.compileState = compileState;
    }

    public boolean isStaged() {
        return staged;
    }

    public void setStaged(boolean staged) {
        this.staged = staged;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(String contentChecksum, SortedMap<DataSourceIdentifier, String> dataSourceChecksum) {
        this.checksum = new Checksum(contentChecksum, dataSourceChecksum);
    }

    public class Checksum {
        private String contentChecksum;
        private SortedMap<DataSourceIdentifier, String> dataSourceChecksum;

        public Checksum(String contentChecksum, SortedMap<DataSourceIdentifier, String> dataSourceChecksum) {
            this.contentChecksum = contentChecksum;
            this.dataSourceChecksum = dataSourceChecksum;
        }

        public String getCombinedChecksum(){
            StringBuilder result = new StringBuilder(contentChecksum);
            for (String checksum : dataSourceChecksum.values()) {
                result.append('-');
                result.append(checksum);
            }
            return result.toString();
        }
    }

    public class ContentPageReference {
        private String parentPath;
        private String contentId;

        public ContentPageReference(String parentPath, String contentId) {
            this.parentPath = parentPath;
            this.contentId = contentId;
        }

        public String getParentPath() {
            return parentPath;
        }

        public String getContentId() {
            return contentId;
        }
    }
}

