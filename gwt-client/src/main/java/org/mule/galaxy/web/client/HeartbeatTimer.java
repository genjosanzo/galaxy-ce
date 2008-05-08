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

package org.mule.galaxy.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

class HeartbeatTimer extends Timer
{
    private Galaxy galaxy;

    private volatile boolean dialogVisible = false;

    // TODO get it from some more global config value?
    private int intervalSeconds = 5;

    public HeartbeatTimer(final Galaxy galaxy)
    {
        this.galaxy = galaxy;
        scheduleRepeating(intervalSeconds * 1000); // accepts ms
    }

    public void run()
    {
        // TODO check how we can provide more client info, and if it makes sense to
        galaxy.getHeartbeatService().ping("web", new AsyncCallback() {

            public void onFailure(final Throwable throwable)
            {
                if (!dialogVisible)
                {
                    SessionKilledDialog panel = new SessionKilledDialog(galaxy, HeartbeatTimer.this);
                } else {
                    // cancel, the dialog will trigger this heartbeat timer again periodically
                    cancel();    
                }

                /*
                    The dialog will call us back to reconnect, cancel this one, otherwise UI values are off.
                    Once the dialog is dismissed, heartbeat will be resumed.
                 */
                dialogVisible = true;

                // problem
                GWT.log("Server is down or session has been killed by the server", throwable);
            }

            public void onSuccess(final Object o)
            {
                // everything is fine, do nothing
                // TODO hide panel, go to app root for login
            }
        });
    }

    public void scheduleRepeating(final int i)
    {
        super.scheduleRepeating(i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public int getIntervalSeconds()
    {
        return intervalSeconds;
    }

    public void onDialogDismissed()
    {
        dialogVisible = false;
    }
}
