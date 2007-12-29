package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.Collection;

public class TransitionResponse implements IsSerializable {
    private boolean success;
    private Collection messages;
    
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public Collection getMessages() {
        return messages;
    }
    public void setMessages(Collection messages) {
        this.messages = messages;
    }
    public void addMessage(String msg, boolean warning) {
        if (messages == null) {
            messages = new ArrayList();
        }
        
        messages.add(new WApprovalMessage(msg, warning));
    }
}
