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

package org.mule.galaxy.web.client.ui.panel;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Adds a style attribute of "display: inline" to every Widget added.
 * This makes the panel behave as a series of &lt;span&gt;s instead of
 * a series of &lt;div&gt;s.
 */
public class InlineFlowPanel extends FlowPanel {

    public void add(Widget w) {
        DOM.setStyleAttribute(w.getElement(), "display", "inline");
        super.add(w);
    }

    public void insert(Widget w, int beforeIndex) {
        DOM.setStyleAttribute(w.getElement(), "display", "inline");
        super.insert(w, beforeIndex);
    }

}
