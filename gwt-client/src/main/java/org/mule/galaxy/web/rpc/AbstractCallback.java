/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
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

package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

import org.mule.galaxy.web.client.ui.panel.ErrorPanel;

public abstract class AbstractCallback<T> implements AsyncCallback<T> {

    private ErrorPanel errorPanel;
    private Timer timer;
    private static final int AUTO_HIDE_DELAY = 3000;

    public AbstractCallback(ErrorPanel panel) {
        this.errorPanel = panel;
    }

    public void onFailureDirect(final Throwable caught) {
        String msg = caught.getMessage();

        if (caught instanceof InvocationException && !(caught instanceof StatusCodeException)) {
            // happens after server is back online, and got a forward to a login page
            // typically would be displayed with a session killed dialog
            setErrorMessage("Current session has been killed, please re-login.");
        } else if (msg != null || !"".equals(msg)) {
            setErrorMessage("Error communicating with server: " + msg + "", true);
        } else {
            setErrorMessage("There was an error communicating with the server. Please try again." + caught.getClass().getName(), false);
        }
    }
    
    public void setErrorMessage(final String message) {
        setErrorMessage(message, false);
    }

    public void setErrorMessage(final String message, final boolean autoHide) {
        errorPanel.setMessage(message);
        if (autoHide) {
            timer = new Timer() {
                @Override
                public void run() {
                    errorPanel.clearErrorMessage();
                }
            };
            timer.schedule(AbstractCallback.AUTO_HIDE_DELAY);
        }
    }
    
    public void onFailure(final Throwable caught) {
        onFailureDirect(caught);
    }

}
