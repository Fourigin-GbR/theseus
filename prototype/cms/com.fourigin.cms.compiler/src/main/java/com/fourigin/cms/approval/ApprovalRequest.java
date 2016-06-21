package com.fourigin.cms.approval;

import java.util.Date;

public interface ApprovalRequest {
    ApprovalRequest getParent();

    Date getDate();


}
