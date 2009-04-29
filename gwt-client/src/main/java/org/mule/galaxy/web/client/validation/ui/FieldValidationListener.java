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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.validation.ValidationListener;

/**
 * A UI validation listener updating a GWT's label with a failure message.
 * <p/>
 * CSS Styles:
 * <ul>
 *  <li>ValidationMessage - label style
 *  <li>FailedValidation - failed widget's style
 * </ul>
 */
public class FieldValidationListener implements ValidationListener {

    private final Label validationLabel;

    public FieldValidationListener(final Label validationLabel) {
        this.validationLabel = validationLabel;
        validationLabel.setStyleName("ValidationMessage");
        validationLabel.setVisible(false);
    }

    public void onSuccess(final ValidationEvent event) {
        Widget source = (Widget) event.source;
        clearError(source);
    }

    public void clearError(Widget source) {
        source.removeStyleName("FailedValidation");
        validationLabel.setVisible(false);
        validationLabel.setText("");
    }

    public void onFailure(final ValidationEvent event) {
        validationLabel.setText(event.message);
        validationLabel.setVisible(true);
        Widget source = (Widget) event.source;
        source.addStyleName("FailedValidation");
    }
}
