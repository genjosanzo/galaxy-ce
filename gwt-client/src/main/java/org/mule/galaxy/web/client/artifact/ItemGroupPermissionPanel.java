package org.mule.galaxy.web.client.artifact;

import java.util.Map;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.admin.AbstractGroupPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WPermissionGrant;

public class ItemGroupPermissionPanel extends AbstractGroupPanel {

    private String itemId;

    public ItemGroupPermissionPanel(RegistryPanel adminPanel, String itemId) {
        super(adminPanel);
        this.itemId = itemId;
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
        menuPanel.getSecurityService().getPermissions(false, callback);
    }

    protected void getGroupPermissionGrants(AbstractCallback callback) {
        menuPanel.getSecurityService().getGroupPermissions(itemId, callback);
    }

    protected void applyPermissions(Map groups2Permissions, AbstractCallback callback) {
        menuPanel.getSecurityService().applyPermissions(itemId, groups2Permissions, callback);
    }

}
