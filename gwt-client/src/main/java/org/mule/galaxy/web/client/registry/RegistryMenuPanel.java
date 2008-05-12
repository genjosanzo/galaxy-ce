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

package org.mule.galaxy.web.client.registry;

import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.util.NavigationUtil;
import org.mule.galaxy.web.client.util.Toolbox;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Forms the basis of any pages which do not list artifacts.
 */
public class RegistryMenuPanel extends MenuPanel {

    public RegistryMenuPanel() {
        this(true, true);
    }

    public RegistryMenuPanel(boolean showBrowse, boolean showSearch) {
        super();
        
        Image addImg = new Image("images/add_obj.gif");
        addImg.addClickListener(NavigationUtil.createNavigatingClickListener("add-artifact"));
        
        Image addWkspcImg = new Image("images/fldr_obj.gif");
        addWkspcImg.addClickListener(NavigationUtil.createNavigatingClickListener("add-workspace"));

        Toolbox topMenuLinks = new Toolbox(false);
        topMenuLinks.add(asHorizontal(addImg, new Label(" "), new Hyperlink("Add Artifact", "add-artifact")));
        
        Hyperlink hl = new Hyperlink("Add Workspace", "add-workspace");
        topMenuLinks.add(asHorizontal(addWkspcImg, new Label(" "), hl));
        
        addOtherLinks(topMenuLinks);
        
        if (showBrowse) {
            hl = new Hyperlink("Browse Workspaces", "browse");
            topMenuLinks.add(hl);
        }

        if (showSearch) {
            hl = new Hyperlink("Search Workspaces", "search");
            topMenuLinks.add(hl);
        }
        
        addMenuItem(topMenuLinks);
    }

    protected void addOtherLinks(Toolbox topMenuLinks) {
        
    }

}
