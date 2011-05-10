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
 * Checks that a String has at least specified length, with optional trimming.
 */
public class MinLengthValidator implements com.extjs.gxt.ui.client.widget.form.Validator {

    protected boolean shouldTrim = true;
    protected int minLength;

    public MinLengthValidator(int minLength) {
        this.minLength = minLength;
    }

    public MinLengthValidator(final int minLength, final boolean shouldTrim) {
        this.shouldTrim = shouldTrim;
        this.minLength = minLength;
    }

    public String validate(Field<?> field, String s) {
        if (validate(s)) {
            return null;
        }
        return "Entry too short. Min " + minLength + " chars";

    }


    public boolean validate(final String s) {
        if (s == null) {
            return false;
        }

        return shouldTrim ? s.trim().length() >= minLength : s.length() >= minLength;
    }

    public boolean isShouldTrim() {
        return shouldTrim;
    }

    public void setShouldTrim(final boolean shouldTrim) {
        this.shouldTrim = shouldTrim;
    }

}