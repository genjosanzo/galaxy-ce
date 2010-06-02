package org.mule.galaxy.policy;

import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ApprovalMessage {
    private boolean warning;
    private String message;
    private List<String> parameters;
    
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

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
