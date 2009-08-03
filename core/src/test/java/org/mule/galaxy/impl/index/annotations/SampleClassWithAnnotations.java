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

package org.mule.galaxy.impl.index.annotations;

@Marker("ClassLevel")
public class SampleClassWithAnnotations
{
    @Marker("FieldLevel")
    private String myField;

    private int anotherNonAnnotatedField;

    @Marker("MethodLevel / Main")
    public static void main(@Marker("ParamLevel")
                            @MultiMarker(value = "ParamLevel", param1 = "12", param2 = "abc")
                            String[] args) throws Exception
    {
        // no-op
    }

    @Override
    @Marker("MethodLevel / toString")
    public String toString()
    {
        return super.toString();
    }

    public void nonAnnotatedMethod()
    {
        // no-op
    }
}
