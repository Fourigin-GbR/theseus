package com.fourigin.argo.controller.editors;

public abstract class AbstractContentElementPointer implements ContentElementPointer {

    @Override
    public void copyFrom(ContentElementPointer otherPointer) {
        if(otherPointer == null){
            throw new IllegalArgumentException("Source pointer must not be null!");
        }

        this.setBase(otherPointer.getBase());
        this.setPath(otherPointer.getPath());
        this.setContentPath(otherPointer.getContentPath());
    }
}
