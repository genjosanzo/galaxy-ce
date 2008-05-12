package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.SimplePanel;
import org.mule.galaxy.web.client.util.AbstractForm;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;

public abstract class AbstractAdministrationForm extends AbstractForm {

    protected AdministrationPanel adminPanel;

    public AbstractAdministrationForm(AdministrationPanel adminPanel, 
                                      String successToken,
                                      String successMessage, 
                                      String deleteMessage) {
        super(adminPanel, successToken, successMessage, deleteMessage);
        
        this.adminPanel = adminPanel;
    }
    
    protected SecurityServiceAsync getSecurityService() {
        return adminPanel.getGalaxy().getSecurityService();
    }
}
