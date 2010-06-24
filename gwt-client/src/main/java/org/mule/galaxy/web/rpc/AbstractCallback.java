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

import org.mule.galaxy.web.client.ui.panel.ErrorPanel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractCallback<T> implements AsyncCallback<T> {

    private final ErrorPanel errorPanel;

    public AbstractCallback(final ErrorPanel panel) {
        this.errorPanel = panel;
    }

    public void onFailureDirect(final Throwable caught) {
        final String msg = caught.getMessage();
        if (caught instanceof InvocationException && !(caught instanceof StatusCodeException)) {
            // happens after server is back online, and got a forward to a login page
            // typically would be displayed with a session killed dialog
            setErrorMessage("Current session has been killed, please re-login.");
        } else if (msg != null && !"".equals(msg)) {
            setErrorMessage("Error communicating with server: " + msg + "");
        } else {
            setErrorMessage("There was an error communicating with the server. Please try again. <br />Exception: " + caught.getClass().getName());
        }
    }

    public void onFailure(final Throwable caught) {
        onFailureDirect(caught);
    }

    /**
    *
    * Display an error message.
    *
    * @see ErrorPanel#setMessage(String)
    * @param message
    */
   public Widget setErrorMessage(final String message) {
       if (errorPanel != null) {
           return errorPanel.setMessage(message);
       }
       return null;
   }

   public void removeMessage(final Widget message) {
       if (errorPanel != null) {
           errorPanel.removeMessage(message);
       }
   }

}
