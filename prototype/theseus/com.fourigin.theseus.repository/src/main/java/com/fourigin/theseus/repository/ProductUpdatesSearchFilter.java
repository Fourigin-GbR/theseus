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

    public Set<String> getProductCodes() {
        return productCodes;
    }

    public void setProductCodes(Set<String> productCodes) {
        this.productCodes = productCodes;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }
}
