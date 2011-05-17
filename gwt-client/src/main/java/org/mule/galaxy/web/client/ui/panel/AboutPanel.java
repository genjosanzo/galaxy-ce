/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-05-14 00:59:35Z mark $
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

package org.mule.galaxy.web.client.ui.panel;

import org.mule.galaxy.web.client.ui.help.GalaxyConstants;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.core.client.GWT;

/**
 * Used to display version and license information
 */
public class AboutPanel extends AbstractInfoPanel {

    private static final GalaxyConstants galaxyMessages = (GalaxyConstants) GWT.create(GalaxyConstants.class);

    public AboutPanel() {
        super();
    }

    public AboutPanel(int height) {
        super(height);
    }

    public String getHeading() {
        return galaxyMessages.aboutSpace();
    }

    @Override
    public Html getText() {
        return new Html();
    }

}
