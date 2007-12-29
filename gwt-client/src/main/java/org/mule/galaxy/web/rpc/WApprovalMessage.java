package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WApprovalMessage implements IsSerializable {
    private String message;
    private boolean warning;
    
    public WApprovalMessage() {
        super();
    }
    public WApprovalMessage(String message, boolean warning) {
        super();
        this.message = message;
        this.warning = warning;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isWarning() {
        return warning;
    }
    public void setWarning(boolean warning) {
        this.warning = warning;
    }
    
}
