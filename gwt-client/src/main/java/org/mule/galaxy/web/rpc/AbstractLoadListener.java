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

package org.mule.galaxy.web.rpc;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.LoadListener;
import org.mule.galaxy.web.client.ErrorPanel;

public abstract class AbstractLoadListener extends LoadListener {
    private ErrorPanel errorPanel;

    public AbstractLoadListener(ErrorPanel panel) {
        super();
        this.errorPanel = panel;
    }

    
    @Override
    public void loaderLoadException(LoadEvent le) {
        Throwable caught = le.exception;
        String msg = caught.getMessage();
        
        if (msg != null || !"".equals(msg)) {
            errorPanel.setMessage("Error communicating with server: " + caught.getMessage() + "");
        } else {
            errorPanel.setMessage("There was an error communicating with the server. Please try again." + caught.getClass().getName());
        }
    }
    
}
