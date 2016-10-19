package com.fourigin.cms.repository;

import com.fourigin.cms.models.datasource.DataSourceIdentifier;
import com.fourigin.cms.models.structure.CompileState;

import java.util.SortedMap;

public class DirectoryInfoItem {
    private String name;
    private String localizedName;
    private String displayName;
    private String description;
    private boolean staged;
    private String checksum;
    private SortedMap<DataSourceIdentifier, String> dataSourceChecksum;

    private CompileState compileState;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStaged() {
        return staged;
    }

    public void setStaged(boolean staged) {
        this.staged = staged;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public SortedMap<DataSourceIdentifier, String> getDataSourceChecksum() {
        return dataSourceChecksum;
    }

    public void setDataSourceChecksum(SortedMap<DataSourceIdentifier, String> dataSourceChecksum) {
        this.dataSourceChecksum = dataSourceChecksum;
    }

    public CompileState getCompileState() {
        return compileState;
    }

    public void setCompileState(CompileState compileState) {
        this.compileState = compileState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectoryInfoItem)) return false;

        DirectoryInfoItem that = (DirectoryInfoItem) o;

        if (staged != that.staged) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (localizedName != null ? !localizedName.equals(that.localizedName) : that.localizedName != null)
            return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (checksum != null ? !checksum.equals(that.checksum) : that.checksum != null) return false;
        //noinspection SimplifiableIfStatement
        if (dataSourceChecksum != null ? !dataSourceChecksum.equals(that.dataSourceChecksum) : that.dataSourceChecksum != null)
            return false;
        return compileState != null ? compileState.equals(that.compileState) : that.compileState == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (localizedName != null ? localizedName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (staged ? 1 : 0);
        result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
        result = 31 * result + (dataSourceChecksum != null ? dataSourceChecksum.hashCode() : 0);
        result = 31 * result + (compileState != null ? compileState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DirectoryInfoItem{" +
            "name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            ", staged=" + staged +
            ", checksum='" + checksum + '\'' +
            ", dataSourceChecksum=" + dataSourceChecksum +
            ", compileState=" + compileState +
            '}';
    }

}
