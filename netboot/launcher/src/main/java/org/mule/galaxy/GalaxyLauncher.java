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

package org.mule.galaxy;

import org.mule.galaxy.netboot.GalaxyClientClassPathConfig;
import org.mule.galaxy.netboot.NetbootConfig;
import org.mule.galaxy.netboot.launch.NamedUrlClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

public class GalaxyLauncher
{
    /**
     * logger used by this class
     */
    public static final String GALAXY_BOOTSTRAP_CLASS_NAME = "org.mule.galaxy.netboot.GalaxyBootstrap";

    public static void main(String[] args) throws Throwable
    {


        NetbootConfig config = new NetbootConfig(args, System.getProperties());
        final GalaxyLauncher launcher = new GalaxyLauncher();
        System.out.println("Mule Netboot started");

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            //@Override
            public void run()
            {
                launcher.shutdown();
            }

        });

        config.validate();
        launcher.launchWithConfig(config);
    }



    public void shutdown()
    {
        System.out.println("Mule Netboot Shutting down...");
    }


    public void launchWithConfig(NetbootConfig config) throws Exception
    {
        config.validate();
        GalaxyClientClassPathConfig classpath = new GalaxyClientClassPathConfig(new File(config.getMuleHome()));

        final List<URL> urlList = classpath.getURLs();
        URL[] galaxyJars = urlList.toArray(new URL[urlList.size()]);

        NamedUrlClassLoader galaxyCl = new NamedUrlClassLoader(galaxyJars, Thread.currentThread().getContextClassLoader());
        galaxyCl.setTag("galaxy");

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(galaxyCl);

        // call out to external class interacting with Galaxy to construct the final set of jar URLs
        Class galaxyBootstrap = Class.forName(GALAXY_BOOTSTRAP_CLASS_NAME, true, Thread.currentThread().getContextClassLoader());
        Method method = galaxyBootstrap.getMethod("constructMuleClasspath", NetbootConfig.class);
        URL[] muleClasspathUrls = new URL[0]; // it's a static method
        try
        {
            muleClasspathUrls = (URL[]) method.invoke(null, config);
        }
        catch (InvocationTargetException e)
        {
            // rethrow a nested root cause
            throw (Exception) e.getCause();
        }

        // dereference explicitly to help GC
        galaxyCl = null;
        method = null;
        galaxyBootstrap = null;

        System.gc();

// create Mule's classloader as a child of whatever called us
        NamedUrlClassLoader muleCl = new NamedUrlClassLoader(muleClasspathUrls, oldCl);
        muleCl.setTag("netboot launcher");
        Thread.currentThread().setContextClassLoader(muleCl);

        // launch Mule
        Object wrapper = Class.forName("org.mule.galaxy.netboot.launch.MuleServerWrapper",
                true, Thread.currentThread().getContextClassLoader()).newInstance();
        WrapperManager.start((WrapperListener) wrapper, config.getStartupArgs());
    }


}
