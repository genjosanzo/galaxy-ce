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

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class MenuPanel extends AbstractErrorShowingComposite {

    private LayoutContainer mainLayoutContainer;
    private Layout layout;
    private LayoutContainer leftMenuContainer;
    private Widget mainWidget;
    private FlowPanel topPanel;
    private Widget topWidget;
    private FlowPanel centerPanel;
    private LayoutContainer leftMenu;
    private BorderLayoutData westData;
    private BorderLayoutData centerData;

    private boolean firstShow = true;

    public MenuPanel() {
        this(true);
    }

    public MenuPanel(boolean left) {
        mainLayoutContainer = new LayoutContainer();
        layout = new BorderLayout();
        westData = new BorderLayoutData(LayoutRegion.WEST, 220);  
        westData.setSplit(true);  
        westData.setCollapsible(true);  
        westData.setMargins(new Margins(0,5,0,0));  

        centerData = new BorderLayoutData(LayoutRegion.CENTER);  
        centerData.setMargins(new Margins(0));

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

            mainLayoutContainer.add(leftMenu, westData);  
            mainLayoutContainer.setLayout(layout);
            mainLayoutContainer.setAutoHeight(false);
            mainLayoutContainer.setHeight("900px");
            mainLayoutContainer.setId("border-layout-container");
        }

        initWidget(mainLayoutContainer);
    }

    public void setId(String id) {
        mainLayoutContainer.getElement().setId(id);
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

		mainLayoutContainer.add(centerPanel, centerData);
		centerPanel.add(getMainPanel());
        
        topPanel = new FlowPanel();
        topPanel.setStyleName("top-panel");
        mainLayoutContainer.layout();
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
