package org.mule.galaxy.wsi;

public class Message {
    private String text;
    private int lineNumber = -1;
    private int columnNumber = -1;
    private String systemId;
    
    public Message(String text, int lineNumber, int columnNumber, String systemId) {
        super();
        this.text = text;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.systemId = systemId;
    }
    
    public Message(String message) {
        this.text = message;
    }

    public String getText() {
        return text;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public int getColumnNumber() {
        return columnNumber;
    }
    
    public String getSystemId() {
        return systemId;
    }
}
