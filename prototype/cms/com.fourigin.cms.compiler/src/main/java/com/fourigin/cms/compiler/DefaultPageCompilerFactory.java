package com.fourigin.cms.compiler;

import java.util.Map;

public class DefaultPageCompilerFactory implements PageCompilerFactory {
    private Map<String, PageCompiler> compilers;

    @Override
    public PageCompiler getInstance(String base) {
        if(compilers == null || compilers.isEmpty()){
            throw new IllegalStateException("No page compiler available at all!");
        }

        PageCompiler compiler = compilers.get(base);
        if(compiler == null){
            throw new IllegalArgumentException("No page compiler available for base '" + base + "'!");
        }

        return compiler;
    }

    public void setCompilers(Map<String, PageCompiler> compilers) {
        this.compilers = compilers;
    }
}
