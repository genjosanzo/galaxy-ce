package org.mule.galaxy;

import org.mule.galaxy.util.Message;


public class GalaxyRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final Message message;
    
    public GalaxyRuntimeException(Message msg) {
        message = msg;
    }
    
    public GalaxyRuntimeException(Message msg, Throwable t) {
        super(t);
        message = msg;
    }
    
    public GalaxyRuntimeException(Throwable cause) {
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
