/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.repository.client.item;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.AbstractGroupPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WPermissionGrant;

public class ItemRolePermissionPanel extends AbstractGroupPanel {

    private String itemId;
    private final int permissionType;
    private final Galaxy galaxy;

    public ItemRolePermissionPanel(Galaxy galaxy,
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

    protected Widget createGrantWidget(WPermissionGrant pg, boolean uberUser) {
        return createGrantWidget(pg);        
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
