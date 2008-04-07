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

package org.mule.galaxy.netboot;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constructs a default set of JAR Urls located under Mule home folder.
 */
public class GalaxyClientClassPathConfig
{
    protected static final String GALAXY_CLIENT_DIR = "/lib/galaxy";
    protected static final String MULE_OPT_DIR = "/lib/opt";

    private List<URL> urls = new ArrayList<URL>();

    /**
     * Constructs a new DefaultMuleClassPathConfig.
     * @param muleHome Mule home directory
     */
    public GalaxyClientClassPathConfig(File muleHome)
    {
        File userDir = new File(muleHome, GALAXY_CLIENT_DIR);
        this.addFile(userDir);
        this.addFiles(this.listJars(userDir));

        // add groovy runtime
        File optDir = new File(muleHome, MULE_OPT_DIR);
        File[] groovy = optDir.listFiles(new FilenameFilter()
        {
            public boolean accept(final File dir, final String name)
            {
                return name.startsWith("groovy");
    }
        });

        this.addFiles(groovy);
    }

    /**
     * Getter for property 'urls'.
     *
     * @return A copy of 'urls'. Items are java.net.URL
     */
    public List<URL> getURLs()
    {
        return new ArrayList<URL>(this.urls);
    }

    /**
     * Setter for property 'urls'.
     *
     * @param urls Value to set for property 'urls'.
     */
    public void addURLs(List<URL> urls)
    {
        if (urls != null && !urls.isEmpty())
        {
            this.urls.addAll(urls);
        }
    }

    /**
     * Add a URL to Mule's classpath.
     *
     * @param url folder (should end with a slash) or jar path
     */
    public void addURL(URL url)
    {
        this.urls.add(url);
    }

    public void addFiles(List<File> files)
    {
        for (File file : files)
        {
            this.addFile(file);
        }
    }

    public void addFiles(File[] files)
    {
        for (File file : files)
        {
            this.addFile(file);
        }
    }

    public void addFile(File jar)
    {
        try
        {
            this.addURL(jar.getAbsoluteFile().toURI().toURL());
        }
        catch (MalformedURLException mux)
        {
            throw new RuntimeException("Failed to construct a classpath URL", mux);
        }
    }

    /**
     * Find and if necessary filter the jars for classpath.
     *
     * @return a list of {@link java.io.File}s
     */
    protected List<File> listJars(File path)
    {
        File[] jars = path.listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                try
                {
                    return pathname.getCanonicalPath().endsWith(".jar");
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });

        return jars == null ? Collections.EMPTY_LIST : Arrays.asList(jars);
    }

}