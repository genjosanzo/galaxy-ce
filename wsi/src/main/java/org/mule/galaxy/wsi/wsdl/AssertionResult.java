package org.mule.galaxy.wsi.wsdl;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.wsi.Message;

public class AssertionResult {
    private String name;
    private boolean failed;
    private List<Message> messages = new ArrayList<Message>();;
    
    public AssertionResult(String name, boolean failed) {
        super();
        this.name = name;
        this.failed = failed;
    }
    public AssertionResult(String name, boolean failed, String message) {
        super();
        this.name = name;
        this.failed = failed;
        messages.add(new Message(message));
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isFailed() {
        return failed;
    }
    public List<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    public void addMessage(String message, String systemId, int lineNumber, int columnNumber) {
        this.messages.add(new Message(message, lineNumber, columnNumber, systemId));
    }
    public void addMessage(String message) {
        this.messages.add(new Message(message));
    }
    public void setFailed(boolean failed) {
        this.failed = failed;
    }
        
}
