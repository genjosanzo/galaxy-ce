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

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuPanel extends AbstractErrorShowingComposite {

    private HorizontalSplitPanel panel;
    private LayoutContainer leftMenuContainer;
    private Widget mainWidget;
    private FlowPanel topPanel;
    private Widget topWidget;
    private LayoutContainer leftMenu;
    private FlowPanel centerPanel;

    private boolean firstShow = true;

    public MenuPanel() {
        this(true);
    }

    public MenuPanel(boolean left) {
        panel = new HorizontalSplitPanel();
        panel.setSplitPosition("220px");

        if (left) {
            // the left panel
            leftMenu = new LayoutContainer();
            leftMenu.setStyleName("left-menu");

            // wrapper/container for menu widgets in the left panel
            leftMenuContainer = new LayoutContainer();
            leftMenuContainer.setLayoutOnChange(true);
            leftMenuContainer.setStyleName("left-menu-container");
            leftMenuContainer.layout(true);
            leftMenuContainer.setMonitorWindowResize(true);

            leftMenu.add(leftMenuContainer);
            leftMenu.layout(false);

            panel.setLeftWidget(leftMenu);
        }

        initWidget(panel);
    }

    public void setId(String id) {
        panel.getElement().setId(id);
    }
    
    @Override
    public void showPage(List<String> params) {
        if (firstShow) {
            firstShow = false;
            onFirstShow();
        }

        if (mainWidget instanceof Showable) {
            ((Showable) mainWidget).showPage(params);
        }
    }

    protected void onFirstShow() {
        centerPanel = new FlowPanel();
        centerPanel.setStyleName("main-application-panel");

        panel.setRightWidget(centerPanel);

        centerPanel.add(getMainPanel());

        topPanel = new FlowPanel();
        topPanel.setStyleName("top-panel");
    }

    public boolean isFirstShow() {
        return firstShow;
    }

    public void hidePage() {
        if (mainWidget instanceof Showable) {
            ((Showable) mainWidget).hidePage();
        }
    }

    public void addMenuItem(Widget widget) {
        leftMenuContainer.add(widget);
    }

    public void addMenuContainer(Widget widget) {
        leftMenuContainer.add(widget);
    }

    public void addMenuItem(Widget widget, int index) {
        leftMenuContainer.insert(widget, index);
    }

    public void removeMenuItem(Widget widget) {
        leftMenuContainer.remove(widget);
    }

    public void setMain(Widget widget) {
        FlowPanel mainPanel = getMainPanel();

        if (mainWidget != null) {
            mainPanel.remove(mainWidget);
        }

        clearErrorMessage();

        this.mainWidget = widget;

        mainPanel.add(widget);
    }

    public void setTop(Widget widget) {
        if (centerPanel.getWidgetIndex(topPanel) == -1) {
            centerPanel.insert(topPanel, 0);
        }

        if (topWidget != null)
            topPanel.remove(topWidget);

        if (widget instanceof Showable) {
            ((Showable) widget).showPage(new ArrayList<String>());
        }
        topWidget = widget;
        if (widget != null) {
            topPanel.add(widget);
        }
    }

    public Widget getMain() {
        return mainWidget;
    }

    public LayoutContainer getLeftMenu() {
        return leftMenu;
    }

    public void setLeftMenu(LayoutContainer leftMenu) {
        this.leftMenu = leftMenu;
    }


}
