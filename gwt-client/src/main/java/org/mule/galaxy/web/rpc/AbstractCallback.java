package org.mule.galaxy.web.rpc;

import org.mule.galaxy.web.client.AbstractMenuPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractCallback implements AsyncCallback{
    public AbstractMenuPanel menuPanel;

    public AbstractCallback(AbstractMenuPanel panel) {
        super();
        this.menuPanel = panel;
    }

    public void onFailure(Throwable caught) {
        String msg = caught.getMessage();
        
        if (msg != null || !"".equals(msg)) {
            menuPanel.setMessage("Error communicating with server: " + caught.getMessage() + ". Please try again.");
        } else {
            menuPanel.setMessage("There was an error communicating with the server. Please try again." + caught.getMessage());
        }
    }
    
    
}
