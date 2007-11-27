package org.mule.galaxy.lifecycle;

public class PhaseApproval {
    private boolean approved;
    private String denialMessage;
    
    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public String getDenialMessage() {
        return denialMessage;
    }
    public void setDenialMessage(String denialMessage) {
        this.denialMessage = denialMessage;
    }
}
