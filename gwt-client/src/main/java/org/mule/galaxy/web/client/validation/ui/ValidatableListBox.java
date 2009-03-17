/*
 * $Id: ValidatableListBox.java 1343 2008-08-27 20:18:57Z mark $
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

package org.mule.galaxy.web.client.validation.ui;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.validation.Validator;

/**
 * A listbox which has an embedded validation label right below it.
 */
public class ValidatableListBox extends AbstractValidatableInputField {

    protected ListBox listBox;
    protected boolean isMultiSelect;

    public boolean validate() {
        return getValidator().validate(listBox.getSelectedIndex());
    }

    public ValidatableListBox(final Validator validator) {
        super(validator);
    }

    public ValidatableListBox(final Validator validator, boolean isMultiSelect) {
        super(validator);
        this.isMultiSelect = isMultiSelect;
    }

    protected Widget createInputWidget() {
        listBox = new ListBox(isMultiSelect);
        return listBox;
    }

    public ListBox getListBox() {
        return listBox;
    }

    public String getValue(int index) {
        return listBox.getValue(index);
    }

    public void setValue(int index, String text) {
        listBox.setValue(index, text);
    }
}
