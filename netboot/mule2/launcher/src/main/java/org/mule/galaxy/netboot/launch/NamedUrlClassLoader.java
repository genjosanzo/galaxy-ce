/*
 * $Id: NamedUrlClassLoader.java 487 2008-02-25 21:50:01Z andrew $
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

package org.mule.galaxy.netboot.launch;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

public class NamedUrlClassLoader extends URLClassLoader
{
    private String creatorThreadName = Thread.currentThread().getName();
    private String tag = "<not tagged>";

    public NamedUrlClassLoader(URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
    }

    public NamedUrlClassLoader(URL[] urls)
    {
        super(urls);
    }

    public NamedUrlClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory)
    {
        super(urls, parent, factory);
    }

    /**
     * Open up the method to make it public. 
     */
    @Override
    public void addURL(final URL url)
    {
        super.addURL(url);
    }

    public String getCreatorThreadName()
    {
        return creatorThreadName;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(final String tag)
    {
        this.tag = tag;
    }

    @Override
    public String toString()
    {
        return super.toString() + "(" + tag + ")" + " created by thread " + creatorThreadName;
    }
}
