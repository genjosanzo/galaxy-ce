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

package org.mule.galaxy.web.client.ui.validator;

import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * Validates that a string represents a valid email address.
 */
public class EmailValidator implements com.extjs.gxt.ui.client.widget.form.Validator {

    public String validate(Field<?> field, String value) {
        if (isValidEmail(value)) {
            return null;
        }
        return "Is not a valid email address";
    }

    public static boolean isValidEmail(String value) {
        return value.matches("^[A-z0-9][\\w\\.\\+\\-]+[A-z0-9]@[A-z0-9][\\w\\.-]*[A-z0-9]\\.[A-z][A-z\\.]*[A-z]$");
    }

}