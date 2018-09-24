package com.fourigin.argo.compiler;

public interface PageCompilerFactory {
    PageCompiler getInstance(String customer, String base);
}
