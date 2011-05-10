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
 * Validates a string against a regular expression.
 */
public class RegexValidator implements com.extjs.gxt.ui.client.widget.form.Validator {

    protected String pattern;

    public RegexValidator(final String pattern) {
        this.pattern = pattern;
    }

    public String validate(Field<?> field, String s) {
        if (validate(s)) {
            return null;
        }
        return "Does not match regex: " + pattern;
    }

    public boolean validate(final Object value) {
        if (value == null) {
            return false;
        }
        return !((String) value).matches(pattern);
    }


}