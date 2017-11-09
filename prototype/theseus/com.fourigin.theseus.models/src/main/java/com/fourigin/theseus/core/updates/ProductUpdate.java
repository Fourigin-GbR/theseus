package com.fourigin.theseus.core.updates;

import java.util.Date;

public class ProductUpdate {
    private String productCode;
    private Date date;
    private String user;
    private ProductChange productChange;
    private ChangeApproval approval;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ProductChange getProductChange() {
        return productChange;
    }

    public void setProductChange(ProductChange productChange) {
        this.productChange = productChange;
    }

    public ChangeApproval getApproval() {
        return approval;
    }

    public void setApproval(ChangeApproval approval) {
        this.approval = approval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductUpdate)) return false;

        ProductUpdate that = (ProductUpdate) o;

        if (!productCode.equals(that.productCode)) return false;
        if (!date.equals(that.date)) return false;
        if (!user.equals(that.user)) return false;
        //noinspection SimplifiableIfStatement
        if (!productChange.equals(that.productChange)) return false;
        return approval != null ? approval.equals(that.approval) : that.approval == null;
    }

    @Override
    public int hashCode() {
        int result = productCode.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + productChange.hashCode();
        result = 31 * result + (approval != null ? approval.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductUpdate{" +
            "productCode='" + productCode + '\'' +
            ", date=" + date +
            ", user='" + user + '\'' +
            ", productChange=" + productChange +
            ", approval=" + approval +
            '}';
    }
}
