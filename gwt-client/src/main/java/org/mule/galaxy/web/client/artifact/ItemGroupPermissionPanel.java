package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.AbstractGroupPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WPermissionGrant;

public class ItemGroupPermissionPanel extends AbstractGroupPanel {

    private String itemId;
    private final int permissionType;
    private final Galaxy galaxy;

    public ItemGroupPermissionPanel(Galaxy galaxy, 
                                    ErrorPanel adminPanel, 
                                    String itemId, 
                                    int PermissionType) {
        super(galaxy, adminPanel);
        this.galaxy = galaxy;
        this.itemId = itemId;
        permissionType = PermissionType;
    }

    protected void setGrant(int row, int col, WPermissionGrant pg) {
        ListBox lb = (ListBox) table.getWidget(row + 1, col);
        
        pg.setGrant(lb.getSelectedIndex() - 1);
    }
    
    protected Widget createGrantWidget(WPermissionGrant pg) {
        ListBox lb = new ListBox();
        lb.addItem("Revoked");
        lb.addItem("Inherited");
        lb.addItem("Granted");
        
        lb.setSelectedIndex(pg.getGrant() + 1);
        
        return lb;
    }
    
    protected void getPermissions(AbstractCallback callback) {
        galaxy.getSecurityService().getPermissions(permissionType, callback);
    }

    protected void getGroupPermissionGrants(AbstractCallback callback) {
        galaxy.getSecurityService().getGroupPermissions(itemId, callback);
    }

    protected void applyPermissions(Map groups2Permissions, AbstractCallback callback) {
        galaxy.getSecurityService().applyPermissions(itemId, groups2Permissions, callback);
    }

}
