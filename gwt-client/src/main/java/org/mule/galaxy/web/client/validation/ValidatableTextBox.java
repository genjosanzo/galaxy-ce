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

package org.mule.galaxy.web.client.validation;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A textbox which has an embedded validation label right below it.
 */
public class ValidatableTextBox extends Composite {

    private Label validationLabel = new Label();
    private FlowPanel holderPanel = new FlowPanel();
    private TextBox textBox = new TextBox();
    private ValidationListener validationListener;


    public ValidatableTextBox() {
        holderPanel.add(textBox);
        holderPanel.add(validationLabel);
        validationLabel.setVisible(false);
        validationLabel.setStyleName("ValidationMessage");

        initWidget(holderPanel);
    }

    /**
     * @return top-most FlowPanel grouping every element
     */
    protected Widget getWidget() {
        return holderPanel;
    }

    public Label getValidationLabel() {
        return validationLabel;
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(final ValidationListener validationListener) {
        this.validationListener = validationListener;
    }
}
