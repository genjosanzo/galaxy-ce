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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Forms the basis for a page that can show error messages at the top.
 */
public abstract class AbstractErrorShowingComposite
        extends AbstractShowable implements ErrorPanel {

    private final ErrorContentPanel errorPanel = new ErrorContentPanel();
    private final FlowPanel mainPanel = new FlowPanel();

    public AbstractErrorShowingComposite() {
        mainPanel.setStyleName("main-panel");
        mainPanel.add(errorPanel);
        errorPanel.hide();
    }

    public void clearErrorMessage() {
        errorPanel.close();
    }

    protected Widget createStringWidget(final String message) {
        return new HTML(message);
    }
    
    public void setMessage(final Widget label) {
        errorPanel.removeAllMessages();
        addMessage(label);
    }

    public Widget setMessage(final String message) {
        Widget w = createStringWidget(message);
        setMessage(w);
        return w;
    }

    public Widget addMessage(String message) {
        Widget w = createStringWidget(message);
        addMessage(createStringWidget(message));
        return w;
    }

    public void addMessage(final Widget message) {
        errorPanel.addMessage(message);
    }

    public void removeMessage(Widget message) {
        errorPanel.remove(message);
        if (errorPanel.getItemCount() == 0) {
            errorPanel.hide();
        }
    }

    protected ErrorContentPanel getErrorPanel() {
       return errorPanel;
    }

    protected FlowPanel getMainPanel() {
        return mainPanel;
    }

}
