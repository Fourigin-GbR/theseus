package com.fourigin.cms.editors;

abstract public class AbstractContentElementPointer implements ContentElementPointer {

    @Override
    public void copyFrom(ContentElementPointer otherPointer) {
        if(otherPointer == null){
            throw new IllegalArgumentException("Source pointer must not be null!");
        }

        this.setSiteStructurePath(otherPointer.getSiteStructurePath());
        this.setContentPath(otherPointer.getContentPath());
    }
}
