package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.SiteStructure;

public interface SiteStructureResolver {
    SiteStructure retrieve(String id);
}