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

package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Toolbox extends Composite {

    private FlowPanel panel;
    private FlowPanel header;
    private FlowPanel buttonPanel;
    private SimplePanel titleHolder;
    
    public Toolbox(boolean onwhite) {
        super();

        FlowPanel base = new FlowPanel();
        base.setStyleName("toolbox");
        
        header = new FlowPanel();
        if (onwhite) {
            header.setStyleName("toolbox-header-onwhite");
        } else {
            header.setStyleName("toolbox-header");
        }
        titleHolder = new SimplePanel();
        titleHolder.setStyleName("toolbox-title");
        base.add(header);
        
        buttonPanel = new FlowPanel();
        buttonPanel.setStyleName("toolbox-buttons");
        header.add(buttonPanel);
        
        SimplePanel body = new SimplePanel();
        body.setStyleName("toolbox-body");
        
        panel = new FlowPanel();
        panel.setStyleName("toolbox-items");
        body.add(panel);
        
        base.add(body);
        
        initWidget(base);
    }
    
    public void addButton(Widget button) {
        buttonPanel.add(button);
    }
    
    public void setTitle(String title) {
        Label titleLbl = new Label(title);
        titleLbl.setStyleName("toolbox-title-label");
        setTitle(titleLbl);
    }

    public void setTitle(Widget titleWidget) {
        if (header.getWidgetIndex(titleHolder) == -1) {
            header.insert(titleHolder, 0);
        }
        titleHolder.clear();
        titleHolder.add(titleWidget);
    }

    public void add(Widget w) {
        add(w, true);
    }

    public void add(Widget w, boolean pad) {
        if (pad) {
            SimplePanel itemEntry = new SimplePanel();
            itemEntry.setStylePrimaryName("toolbox-item-entry");
            itemEntry.add(w);
            panel.add(itemEntry);
        } else {
            panel.add(w);
        }
    }
    
    public void clear() {
        panel.clear();
    }
}
