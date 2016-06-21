package com.fourigin.cms.models.structure.nodes;

import com.fourigin.cms.models.datasource.DataSourceIdentifier;
import com.fourigin.cms.models.structure.CompileState;

import java.util.SortedMap;

public class SitePage extends AbstractSiteNode implements SiteNode {
    private CompileState compileState;
    private boolean staged;
    private Checksum checksum;

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

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    public class Checksum {
        private String contentChecksum;
        private SortedMap<DataSourceIdentifier, String> dataSourceChecksum;

        public String getCombinedChecksum(){
            StringBuilder result = new StringBuilder(contentChecksum);
            for (String checksum : dataSourceChecksum.values()) {
                result.append('-');
                result.append(checksum);
            }
            return result.toString();
        }
    }
}

