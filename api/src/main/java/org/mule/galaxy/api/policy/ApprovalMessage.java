package org.mule.galaxy.api.policy;

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

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("ApprovalMessage");
        sb.append("{message='").append(message).append('\'');
        sb.append(", warning=").append(warning);
        sb.append('}');
        return sb.toString();
    }


}
