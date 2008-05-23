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
 * Validates that a string represents a valid email address.
 */
public class EmailValidator extends RegexValidator {

    protected static final String JAVASCRIPT_EMAIL_REGEX = "^[A-z][\\w\\.\\+\\-]+[A-z0-9]@[A-z0-9][\\w\\.-]*[A-z0-9]\\.[A-z][A-z\\.]*[A-z]$";

    public EmailValidator() {
        super(JAVASCRIPT_EMAIL_REGEX);
    }

    public String getFailureMessage() {
        return "Is not a valid email address";
    }
}