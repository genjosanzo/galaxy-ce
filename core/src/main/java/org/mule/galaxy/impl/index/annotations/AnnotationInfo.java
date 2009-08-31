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

import java.util.ArrayList;
import java.util.List;

class AnnotationInfo
{
    public String className;
    public List<NameValue> params = new ArrayList<NameValue>();
    public String method;
    
    static class NameValue
    {
        public String name;
        public Object value;

        NameValue(final String name, final Object value)
        {
            this.name = name;
            this.value = value;
        }

        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            final NameValue nameValue = (NameValue) o;

            if (!name.equals(nameValue.name))
            {
                return false;
            }
            if (!value.equals(nameValue.value))
            {
                return false;
            }

            return true;
        }

        public int hashCode()
        {
            int result;
            result = name.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }

        @Override
        public String toString()
        {
            return String.format("%s=%s", name, value);
        }
    }

    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final AnnotationInfo that = (AnnotationInfo) o;

        if (!className.equals(that.className))
        {
            return false;
        }
        if (params != null ? !params.equals(that.params) : that.params != null)
        {
            return false;
        }
        if (method != null ? !method.equals(that.method) : that.method != null)
        {
            return false;
        }


        return true;
    }

    public int hashCode()
    {
        int result;
        result = className.hashCode();
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(params.size() * 20);
        sb.append(className).append('(');
        for (int i = 0; i < params.size(); i++)
        {
            NameValue param = params.get(i);
            sb.append(param.name).append('=').append(param.value);
            if (i < params.size() - 1)
            {
                sb.append(',');
            } else
            {
                sb.append(')');
            }
        }
        return sb.toString();
    }
}
