package com.fourigin.argo.repository;

public interface DataSourceIndexResolverFactory {
    DataSourceIndexResolver getInstance(String customer, String base);
}
