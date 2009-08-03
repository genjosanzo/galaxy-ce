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

public class SearchPredicate implements IsSerializable {
    public static final int HAS_VALUE           = 0;
    public static final int LIKE                = 1;
    public static final int DOES_NOT_HAVE_VALUE = 2;
    
    private String property;
    private int matchType;
    private String value;
    
    public SearchPredicate() {
    }
    
    public SearchPredicate(String property, int matchType, String value) {
        this.property   = property;
        this.matchType  = matchType;
        this.value      = value;
    }
    
    public String getProperty() {
        return property;
    }
    
    public void setProperty(String property) {
        this.property = property;
    }
    
    public int getMatchType() {
        return matchType;
    }
    
    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
