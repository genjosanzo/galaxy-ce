package org.mule.galaxy.policy;

import java.util.ArrayList;
import java.util.List;

public class Approval {
    private boolean approved;
    private List<String> messages = new ArrayList<String>();
    
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
