package com.fourigin.theseus.core.updates;

import java.util.Date;

public class ChangeApproval {
    private ApprovalType type;
    private Date date;
    private String user;

    public ApprovalType getType() {
        return type;
    }

    public void setType(ApprovalType type) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChangeApproval)) return false;

        ChangeApproval that = (ChangeApproval) o;

        if (type != that.type) return false;
        //noinspection SimplifiableIfStatement
        if (!date.equals(that.date)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChangeApproval{" +
            "type=" + type +
            ", date=" + date +
            ", user='" + user + '\'' +
            '}';
    }
}
