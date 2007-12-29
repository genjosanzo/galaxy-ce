package org.mule.galaxy.policy;

import java.util.ArrayList;
import java.util.List;

public class ApprovalMessage {
    private boolean warning;
    private String message;
    
    public ApprovalMessage(String message) {
        this.message = message;
    }

    public ApprovalMessage(String message, boolean warning) {
        this.message = message;
        this.warning = warning;
    }
    
    public boolean isWarning() {
        return warning;
    }
    public void setWarning(boolean warning) {
        this.warning = warning;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
