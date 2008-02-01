package org.mule.galaxy;

import org.mule.galaxy.util.Message;


public class GalaxyException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private final Message message;
    
    public GalaxyException(Message msg) {
        message = msg;
    }
    
    public GalaxyException(Message msg, Throwable t) {
        super(t);
        message = msg;
    }
    
    public GalaxyException(Throwable cause) {
        super(cause);
        message = null;
    } 
    

    public String getCode() {
        if (null != message) {
            return message.getCode();
        }
        return null;
    }
    
    public String getMessage() {
        if (null != message) {
            return message.toString();
        }
        return null;
    }
}
