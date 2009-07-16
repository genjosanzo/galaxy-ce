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

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
            leftMenu = new FlowPanel() {
                protected void onLoad() {

                    Element br = DOM.createElement("br");
                    DOM.setElementAttribute(br, "class", "clearit");
                    DOM.appendChild(DOM.getParent(this.getElement()), br);
                }
            };
            leftMenu.setStyleName("left-menu");

            panel.setLeftWidget(leftMenu);

            leftMenuContainer = new FlowPanel() {

                protected void onLoad() {

                    Element br = DOM.createElement("br");
                    DOM.setElementAttribute(br, "class", "clearit");
                    DOM.appendChild(DOM.getParent(this.getElement()), br);
                }

            };
            leftMenuContainer.setStyleName("left-menu-container");

            leftMenu.add(leftMenuContainer);
        }

        initWidget(panel);
    }

    public void onShow(List<String> params) {
        if (firstShow) {
            firstShow = false;
            onFirstShow();
        }

        if (mainWidget instanceof AbstractComposite) {
            ((AbstractComposite) mainWidget).onShow(params);
        }
    }

    protected void onFirstShow() {
        centerPanel = new FlowPanel();
        panel.setRightWidget(centerPanel);

        centerPanel.add(getMainPanel());

        topPanel = new FlowPanel();
        topPanel.setStyleName("top-panel");
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

        if (widget instanceof AbstractComposite) {
            ((AbstractComposite) widget).onShow();
        }
        topWidget = widget;
        if (widget != null) {
            topPanel.add(widget);
        }
    }

    public Widget getMain() {
        return mainWidget;
    }

    protected void createLinkWithAdd(Toolbox manageBox,
                                     String title,
                                     String tokenBase,
                                     AbstractComposite list,
                                     AbstractComposite form) {

        Hyperlink link = new Hyperlink(title, tokenBase);
        Hyperlink addLink = new Hyperlink("Add", tokenBase + "/new");

        createDivWithAdd(manageBox, link, addLink);
        createPageInfo(tokenBase, list);
        createPageInfo(tokenBase + "/" + Galaxy.WILDCARD, form);
    }

    /*
     * Render a listview inside a container. The is the GXT version of createLinkWithAdd
     */

    protected LayoutContainer createNavMeunContainer(LayoutContainer c, List<NavMenuItem> items) {

        // store for all menu items in container
        ListStore<NavMenuItem> ls = new ListStore<NavMenuItem>();
        ls.add(items);

        ListView<NavMenuItem> lv = new ListView<NavMenuItem>();
        lv.setDisplayProperty("title"); // from item
        lv.setStore(ls);

        for (final NavMenuItem item : ls.getModels()) {

            // add contextual menul
            if (item.getFormPanel() != null) {
                Menu contextMenu = new Menu();
                contextMenu.setWidth(100);

                MenuItem add = new MenuItem();
                add.setText("Add");
                add.addSelectionListener(new SelectionListener<MenuEvent>() {
                    public void componentSelected(MenuEvent ce) {
                        History.newItem(item.getTokenBase() + NavMenuItem.NEW);
                    }
                });
                contextMenu.add(add);
                lv.setContextMenu(contextMenu);
            }

            lv.addListener(Events.Select, new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    ListViewEvent lve = (ListViewEvent) be;
                    NavMenuItem nmi = (NavMenuItem) lve.getModel();
                    History.newItem(nmi.getTokenBase());
                }
            });

        }

        c.add(lv);
        return c;
    }


    protected void createLinkWithAdd(String tokenBase,
                                     AbstractComposite list,
                                     AbstractComposite form) {

        createPageInfo(tokenBase, list);
        createPageInfo(tokenBase + "/" + Galaxy.WILDCARD, form);
    }

    protected abstract void createPageInfo(String token, final AbstractComposite composite);

    protected void createDivWithAdd(Toolbox manageBox, Hyperlink link, Hyperlink add) {
        InlineFlowPanel item = new InlineFlowPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));

        manageBox.add(item);
    }


}
