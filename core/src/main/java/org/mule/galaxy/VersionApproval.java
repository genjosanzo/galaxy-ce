package org.mule.galaxy;

import java.util.List;

public class VersionApproval {
    private boolean approved;
    private List<String> messages;
    
    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public List<String> getMessages() {
        return messages;
    }
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
    
}
