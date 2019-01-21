package com.fourigin.argo.controller.system;

import java.io.Serializable;

public class UpdateResult implements Serializable {
    private static final long serialVersionUID = -1126872719788841817L;

    private boolean success;
    private String message;

    public static UpdateResult ok(){
        UpdateResult result = new UpdateResult();
        result.setSuccess(true);
        return result;
    }

    public static UpdateResult failed(String message){
        UpdateResult result = new UpdateResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UpdateResult{" +
            "success=" + success +
            ", message='" + message + '\'' +
            '}';
    }
}
