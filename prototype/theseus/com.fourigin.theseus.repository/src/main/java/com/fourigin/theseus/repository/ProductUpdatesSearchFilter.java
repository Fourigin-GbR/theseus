package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.updates.ApprovalType;

import java.util.Date;
import java.util.Set;

public class ProductUpdatesSearchFilter {
    private Set<String> productCodes;
    private Date startDate;
    private Date endDate;
    private String username;
    private ApprovalType approvalType;
}
