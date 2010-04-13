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

package org.mule.galaxy.web.client.ui.field;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Subclasses should provide a widget with user input by overriding
 * {@link #createInputWidget}.
 */
public abstract class AbstractValidatableWidget extends Composite {

    /**
     * Should be called once when constructing a custom widget. Subclasses
     * should save a reference to the created widget if need to.
     *
     * @return Widget containing a user input
     */
    protected abstract Widget createInputWidget();

    /**
     * Trigger the UI component validation.
     *
     * @return true if no validation errors occured
     */
    public abstract boolean validate();
}
