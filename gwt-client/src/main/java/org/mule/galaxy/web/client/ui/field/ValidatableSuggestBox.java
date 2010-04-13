/*
 * $Id: ValidatableTextBox.java 1343 2008-08-20 20:18:57Z dandiep $
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

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A SuggestBox which has an embedded validation label right below it.
 */
public class ValidatableSuggestBox extends AbstractValidatableInputField {

    protected SuggestBox suggestBox;
    private final SuggestOracle oracle;
    private TextBox textBox;

    public ValidatableSuggestBox(final Validator validator, final SuggestOracle oracle) {
        super();
        this.oracle = oracle;

        init(validator);
    }

    public boolean validate() {
        return getValidator().validate(suggestBox.getText());
    }

    protected Widget createInputWidget() {
        textBox = new TextBox();
        suggestBox = new SuggestBox(oracle, textBox);
        return suggestBox;
    }

    public SuggestBox getSuggestBox() {
        return suggestBox;
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public String getText() {
        return suggestBox.getText();
    }

    public void setText(String text) {
        suggestBox.setText(text);
    }
}
