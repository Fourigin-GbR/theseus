package com.fourigin.argo.approval;

import java.util.Date;

public interface ApprovalRequest {
    ApprovalRequest getParent();

    Date getDate();


}
