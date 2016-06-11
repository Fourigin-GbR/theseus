package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.SiteStructure;

public interface SiteStructureRepository extends SiteStructureResolver {
    void create(SiteStructure siteStructure);
    void update(SiteStructure siteStructure);
    void delete(String id);
}
