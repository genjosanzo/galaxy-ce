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

public class LinkInfo implements IsSerializable {
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_ENTRY_VERSION = 1;
    public static final int TYPE_NOT_FOUND = 2;
    
    private boolean autoDetected;
    private String itemName;
    private String itemId;
    private int itemType;
    private boolean reciprocal;
    private String linkId;
    
    public LinkInfo() {
        super();
    }

    public LinkInfo(String linkId, boolean autoDetected, String itemId, String itemName, int itemType, boolean reciprocal) {
        super();
        this.linkId = linkId;
        this.autoDetected = autoDetected;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.reciprocal = reciprocal;
    }

    public boolean isAutoDetected() {
        return autoDetected;
    }

    public void setAutoDetected(boolean autoDetected) {
        this.autoDetected = autoDetected;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isReciprocal() {
        return reciprocal;
    }

    public void setReciprocal(boolean reciprocal) {
        this.reciprocal = reciprocal;
    }

    public String getLinkId() {
        return linkId;
    }
}
