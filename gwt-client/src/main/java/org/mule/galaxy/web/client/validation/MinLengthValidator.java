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
 * Checks that a String has at least specified length, with optional trimming.
 */
public class MinLengthValidator implements Validator {

    protected boolean shouldTrim = true;
    protected int minLength;

    public MinLengthValidator(int minLength) {
        this.minLength = minLength;
    }

    public MinLengthValidator(final int minLength, final boolean shouldTrim) {
        this.shouldTrim = shouldTrim;
        this.minLength = minLength;
    }

    public boolean validate(final Object value) {
        if (value == null) {
            return false;
        }

        if (!(value instanceof String)) {
            // GWT doesn't emulate Object.getClass()
            throw new IllegalArgumentException("This validator accepts Strings only. Got " + value.toString());
        }

        String s = (String) value;
        return shouldTrim ? s.trim().length() >= minLength : s.length() >= minLength;
    }

    public String getFailureMessage() {
        return "Entry too short. Min " + minLength + " chars";
    }

    public boolean isShouldTrim() {
        return shouldTrim;
    }

    public void setShouldTrim(final boolean shouldTrim) {
        this.shouldTrim = shouldTrim;
    }
}