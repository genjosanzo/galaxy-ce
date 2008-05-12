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

package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Panel which puts a title up top with the "title" style.
 */
public abstract class AbstractTitledComposite extends AbstractComposite {

    protected SimplePanel title;
    
    protected void initWidget(Widget widget) {
        FlowPanel titlePanel = new FlowPanel();
        
        title = new SimplePanel();
        titlePanel.add(title);
        
        titlePanel.add(widget);
        
        super.initWidget(titlePanel);
    }

    public void setTitle(String titleText) {
        title.clear();
        title.add(createTitle(titleText));
        
        super.setTitle(titleText);
    }

}
