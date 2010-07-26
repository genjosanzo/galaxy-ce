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

package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.extjs.gxt.ui.client.data.BeanModelTag;

public class WPermissionGrant implements IsSerializable, BeanModelTag {
    public static final int GRANTED = 1;
    public static final int INHERITED = 0;
    public static final int REVOKED = -1;
    
    private WPermission permission;
    private int grant;
    
    public WPermission getPermission() {
        return permission;
    }
    public void setPermission(WPermission permission) {
        this.permission = permission;
    }
    public int getGrant() {
        return grant;
    }
    public void setGrant(int grant) {
        this.grant = grant;
    }
}
