/*
 * $Id$
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

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.validation.Validator;

/**
 * A textarea which has an embedded validation label right below it.
 */
public class ValidatableTextArea extends AbstractValidatableInputField {

    protected TextArea textArea;

    public ValidatableTextArea(final Validator validator) {
        super(validator);
    }

    public boolean validate() {
        return getValidator().validate(textArea.getText());
    }

    protected Widget createInputWidget() {
        textArea = new TextArea();
        return textArea;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public String getText() {
        return textArea.getText();
    }
}