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

import junit.framework.TestCase;

public class ChainedValidatorTest extends TestCase {

    public void testFirstFails() {
        ChainedValidator v = new ChainedValidator(new AlwaysFailValidator("1"), new AlwaysPassValidator("2"));
        assertFalse(v.validate(null));

        assertEquals("1", v.getFailureMessage());
    }

    public void testAllPass() {
        ChainedValidator v = new ChainedValidator(new AlwaysPassValidator("1"), new AlwaysPassValidator("2"));
        assertTrue(v.validate(null));

        assertEquals("2", v.getFailureMessage());
    }

    public void testLastFails() {

        ChainedValidator v = new ChainedValidator(new AlwaysPassValidator("1"), new AlwaysFailValidator("2"));
        assertFalse(v.validate(null));

        assertEquals("2", v.getFailureMessage());
    }

}
