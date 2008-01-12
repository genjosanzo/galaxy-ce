package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.mule.galaxy.web.client.ErrorPanel;

public abstract class AbstractCallback implements AsyncCallback{
    public ErrorPanel menuPanel;

    public AbstractCallback(ErrorPanel panel) {
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
