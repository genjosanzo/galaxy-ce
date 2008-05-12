package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;

public class AbstractAdministrationComposite extends AbstractFlowComposite {
    protected AdministrationPanel adminPanel;

    public AbstractAdministrationComposite(AdministrationPanel a) {
        super();
        this.adminPanel = a;
    }
    
    protected SecurityServiceAsync getSecurityService() {
        return adminPanel.getGalaxy().getSecurityService();
    }
}
