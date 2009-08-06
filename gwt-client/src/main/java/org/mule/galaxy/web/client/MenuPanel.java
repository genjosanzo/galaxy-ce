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
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuPanel extends AbstractErrorShowingComposite {

    private HorizontalSplitPanel panel;
    private FlowPanel leftMenuContainer;
    private Widget mainWidget;
    private FlowPanel topPanel;
    private Widget topWidget;
    private FlowPanel leftMenu;
    private FlowPanel centerPanel;
    private boolean firstShow = true;

    public MenuPanel() {
        this(true);
    }

    public MenuPanel(boolean left) {
        panel = new HorizontalSplitPanel();
        panel.setSplitPosition("220px");

        if (left) {
            leftMenu = new FlowPanel();
            leftMenu.setStyleName("left-menu");

            panel.setLeftWidget(leftMenu);

            leftMenuContainer = new FlowPanel();
            leftMenuContainer.setStyleName("left-menu-container");
            leftMenu.add(leftMenuContainer);
        }

        initWidget(panel);
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

    /*
    protected void createLinkWithAdd(Toolbox manageBox,
                                     String title,
                                     String tokenBase,
                                     AbstractShowable list,
                                     AbstractShowable form) {

        Hyperlink link = new Hyperlink(title, tokenBase);
        Hyperlink addLink = new Hyperlink("Add", tokenBase + "/new");

        createDivWithAdd(manageBox, link, addLink);
        createPageInfo(tokenBase, list);
        createPageInfo(tokenBase + "/" + Galaxy.WILDCARD, form);
    }


    protected void createLinkWithAdd(String tokenBase,
                                     AbstractShowable list,
                                     AbstractShowable form) {

        createPageInfo(tokenBase, list);
        createPageInfo(tokenBase + "/" + Galaxy.WILDCARD, form);
    }

    protected void createDivWithAdd(Toolbox manageBox, Hyperlink link, Hyperlink add) {
        InlineFlowPanel item = new InlineFlowPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));

        manageBox.add(item);
    }
    */

    protected abstract void createPageInfo(String token, final WidgetHelper composite);


}
