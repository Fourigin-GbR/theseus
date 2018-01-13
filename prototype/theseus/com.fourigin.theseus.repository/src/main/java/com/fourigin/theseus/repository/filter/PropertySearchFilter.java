package com.fourigin.theseus.repository.filter;

public class PropertySearchFilter {
    private String searchKey;

    public static PropertySearchFilter forSearchKey(String key){
        return new Builder()
            .withSearchKey(key)
            .build();
    }

    public String getSearchKey() {
        return searchKey;
    }

    static class Builder {
        private String searchKey;

        public Builder withSearchKey(String searchKey){
            this.searchKey = searchKey;

            return this;
        }

        public PropertySearchFilter build(){
            PropertySearchFilter result = new PropertySearchFilter();

            result.searchKey = searchKey;

            return result;
        }
    }
}
