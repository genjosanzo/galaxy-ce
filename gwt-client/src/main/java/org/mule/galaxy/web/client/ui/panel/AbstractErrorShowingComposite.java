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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Forms the basis for a page that can show error messages at the top.
 */
public class AbstractErrorShowingComposite
        extends AbstractShowable implements ErrorPanel {

    private FlowPanel errorPanel;
    //private ErrorContentPanel errorPanel;

    private FlowPanel mainPanel;

    public AbstractErrorShowingComposite() {
        super();

        mainPanel = new FlowPanel();
        mainPanel.setStyleName("main-panel");

        //errorPanel = new ErrorContentPanel();
        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");
    }

    public void clearErrorMessage() {
        //errorPanel.clearState();
        errorPanel.clear();
        mainPanel.remove(errorPanel);
    }

    public void setMessage(Widget label) {
        //errorPanel.clearState();
        errorPanel.clear();
        addMessage(label);
    }

    public void setMessage(String string) {
        setMessage(new Label(string));
    }

    public void addMessage(String message) {
        addMessage(new Label(message));
    }

    public void addMessage(Widget message) {
        int pos = getErrorPanelPosition();
        if (pos > mainPanel.getWidgetCount()) {
            pos = mainPanel.getWidgetCount();
        }
        errorPanel.add(message);
        if (!errorPanel.isAttached()) {
            mainPanel.insert(errorPanel, pos);
        }
    }

    protected int getErrorPanelPosition() {
        return 0;
    }

    //protected ErrorContentPanel getErrorPanel() {
    //   return errorPanel;
    //}

    protected FlowPanel getErrorPanel() {
        return errorPanel;
    }

    protected FlowPanel getMainPanel() {
        return mainPanel;
    }

}
