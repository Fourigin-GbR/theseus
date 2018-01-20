package com.fourigin.argo.editors;

public abstract class AbstractContentElementPointer implements ContentElementPointer {

    @Override
    public void copyFrom(ContentElementPointer otherPointer) {
        if(otherPointer == null){
            throw new IllegalArgumentException("Source pointer must not be null!");
        }

        this.setBase(otherPointer.getBase());
        this.setSiteStructurePath(otherPointer.getSiteStructurePath());
        this.setContentPath(otherPointer.getContentPath());
    }
}
