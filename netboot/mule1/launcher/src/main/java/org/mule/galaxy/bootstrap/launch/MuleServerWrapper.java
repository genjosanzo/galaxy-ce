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

package org.mule.galaxy.bootstrap.launch;

import java.lang.reflect.Method;
import java.net.URL;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 * TODO this class may go once we handle the boot module libraries 
 */
public class MuleServerWrapper implements WrapperListener
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public MuleServerWrapper()
    {
        super();
    }

    /*---------------------------------------------------------------
     * WrapperListener Methods
     *-------------------------------------------------------------*/
    /**
     * The start method is called when the WrapperManager is signaled by the native
     * wrapper code that it can start its application. This method call is expected
     * to return, so a new thread should be launched if necessary.
     *
     * @param args List of arguments used to initialize the application.
     * @return Any error code if the application should exit on completion of the
     *         start method. If there were no problems then this method should return
     *         null.
     */
    public Integer start(String[] args)
    {
        try
        {
            // we don't want to introduce a compile-time dependency on Mule
            // MuleServer.main(args);
            final Class muleServer = Class.forName("org.mule.MuleServer", true, Thread.currentThread().getContextClassLoader());
            Method method = muleServer.getMethod("main", String[].class);
            method.invoke(null, new Object[] {args}); // it's a static method

            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new Integer(1);
        }
    }

    /**
     * Called when the application is shutting down. The Wrapper assumes that this
     * method will return fairly quickly. If the shutdown code code could potentially
     * take a long time, then WrapperManager.signalStopping() should be called to
     * extend the timeout period. If for some reason, the stop method can not return,
     * then it must call WrapperManager.stopped() to avoid warning messages from the
     * Wrapper.
     *
     * @param exitCode The suggested exit code that will be returned to the OS when
     *            the JVM exits.
     * @return The exit code to actually return to the OS. In most cases, this should
     *         just be the value of exitCode, however the user code has the option of
     *         changing the exit code if there are any problems during shutdown.
     */
    public int stop(int exitCode)
    {
        return exitCode;
    }

    /**
     * Called whenever the native wrapper code traps a system control signal against
     * the Java process. It is up to the callback to take any actions necessary.
     * Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT,
     * WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or
     * WRAPPER_CTRL_SHUTDOWN_EVENT
     *
     * @param event The system control signal.
     */
    public void controlEvent(int event)
    {
        if (WrapperManager.isControlledByNativeWrapper())
        {
            // The Wrapper will take care of this event
        }
        else
        {
            // We are not being controlled by the Wrapper, so
            // handle the event ourselves.
            if ((event == WrapperManager.WRAPPER_CTRL_C_EVENT)
                || (event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT)
                || (event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT))
            {
                WrapperManager.stop(0);
            }
        }
    }
}
