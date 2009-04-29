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

/**
 * A generic validator implementation tying up a validator, validation listener and an object requesting a validation
 * (typically a widget, but can be any).
 */
public class CallbackValidator implements Validator {

    protected ValidationListener delegateListener;
    protected Validator delegateValidator;
    protected Object source;

    public CallbackValidator(final Validator validator, final ValidationListener listener, final Object source) {
        delegateValidator = validator;
        delegateListener = listener;
        this.source = source;
    }

    public boolean validate(final Object value) {
        boolean isValid = true;

        if (delegateValidator.validate(value)) {
            delegateListener.onSuccess(new ValidationListener.ValidationEvent(source, ""));
        } else {
            isValid = false;
            delegateListener.onFailure(new ValidationListener.ValidationEvent(source, delegateValidator.getFailureMessage()));
        }

        return isValid;
    }

    public String getFailureMessage() {
        return delegateValidator.getFailureMessage();
    }
}
