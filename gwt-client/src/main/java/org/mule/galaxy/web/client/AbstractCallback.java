package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractCallback implements AsyncCallback{
    public AbstractMenuPanel panel;

    public AbstractCallback(AbstractMenuPanel panel) {
        super();
        this.panel = panel;
    }

    public void onFailure(Throwable caught) {
        String msg = caught.getMessage();
        
        if (msg != null || !"".equals(msg)) {
            panel.setMessage("Error communicating with server: " + caught.getMessage() + ". Please try again.");
        } else {
            panel.setMessage("There was an error communicating with the server. Please try again." + caught.getMessage());
        }
    }
    
    
}
