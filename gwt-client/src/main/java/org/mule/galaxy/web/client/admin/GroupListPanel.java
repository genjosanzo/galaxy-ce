package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WPermissionGrant;

public class GroupListPanel extends AbstractGroupPanel {

    private final AdministrationPanel adminPanel;

    public GroupListPanel(AdministrationPanel a) {
        super(a.getGalaxy(), a);
        this.adminPanel = a;
        a.getGalaxy().createPageInfo("groups/*", new GroupForm(adminPanel), a.getGalaxy().getAdminTab());
    }
    
    protected void setGrant(int row, int col, WPermissionGrant pg) {
        CheckBox cb = (CheckBox) table.getWidget(row + 1, col);
        
        if (cb.isChecked()) {
            pg.setGrant(WPermissionGrant.GRANTED);
        } else {
            pg.setGrant(WPermissionGrant.REVOKED);
        }
    }
    
    protected Widget createGrantWidget(WPermissionGrant pg) {
        CheckBox cb = new CheckBox();
        if (pg.getGrant() == WPermissionGrant.GRANTED) {
            cb.setChecked(true);
        }
        return cb;
    }
    
    protected void getPermissions(AbstractCallback callback) {
        getSecurityService().getPermissions(SecurityService.GLOBAL_PERMISSIONS, callback);
    }

    private SecurityServiceAsync getSecurityService() {
        return adminPanel.getSecurityService();
    }

    protected void getGroupPermissionGrants(AbstractCallback callback) {
        getSecurityService().getGroupPermissions(callback);
    }

    protected void applyPermissions(Map groups2Permissions, AbstractCallback callback) {
        getSecurityService().applyPermissions(groups2Permissions, callback);
    }

}
