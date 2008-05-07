package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WPermissionGrant;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

public class GroupPanel extends AbstractGroupPanel {

    public GroupPanel(AdministrationPanel adminPanel) {
        super(adminPanel);
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
        menuPanel.getSecurityService().getPermissions(SecurityService.GLOBAL_PERMISSIONS, callback);
    }

    protected void getGroupPermissionGrants(AbstractCallback callback) {
        menuPanel.getSecurityService().getGroupPermissions(callback);
    }

    protected void applyPermissions(Map groups2Permissions, AbstractCallback callback) {
        menuPanel.getSecurityService().applyPermissions(groups2Permissions, callback);
    }

}
