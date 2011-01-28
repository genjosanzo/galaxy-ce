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

package org.mule.galaxy.web.client.ui.field;

import java.util.Collection;

import com.google.gwt.user.client.Window;

public class StringListBox extends AbstractUserModifiableListBox {

    public StringListBox(Collection<String> list) {
        super(list, null);
    }

    public StringListBox(Collection<String> list, Validator v) {
        super(list, v);
    }

    protected boolean isValid(String text) {
        if ("".equals(text)) {
            Window.alert("You must enter a value!");
            return false;
        }
        return true;
    }


}
