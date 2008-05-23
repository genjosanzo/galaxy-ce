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
 * Checks that a String is less or equal to a specified length, with optional trimming.
 */
public class MaxLengthValidator implements Validator {

    protected boolean shouldTrim = true;
    protected int maxLength;

    public MaxLengthValidator(int maxLength) {
        this.maxLength = maxLength;
    }

    public MaxLengthValidator(final int maxLength, final boolean shouldTrim) {
        this.shouldTrim = shouldTrim;
        this.maxLength = maxLength;
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
        return shouldTrim ? s.trim().length() <= maxLength : s.length() <= maxLength;
    }

    public String getFailureMessage() {
        return "Entry too long. Max " + maxLength + " chars";
    }

    public boolean isShouldTrim() {
        return shouldTrim;
    }

    public void setShouldTrim(final boolean shouldTrim) {
        this.shouldTrim = shouldTrim;
    }
}