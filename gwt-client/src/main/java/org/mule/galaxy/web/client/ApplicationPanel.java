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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;

public abstract class ApplicationPanel extends AbstractErrorShowingComposite {

    private FlowPanel leftMenuContainer;
    private Widget mainWidget;
    private FlowPanel topPanel;
    private Widget topWidget;
    private FlowPanel leftMenu;
    private boolean firstShow = true;

    private LayoutContainer base;
    private ContentPanel centerPanel;
    private ContentPanel westPanel;
    private BorderLayoutData centerData;
    private BorderLayoutData westData;
    private BorderLayout layout;

    public ApplicationPanel() {
        this(true);
    }

    public ApplicationPanel(boolean left) {

        // wrapper for the left nav (optional) and the main app body
        layout = new BorderLayout();

        base = new LayoutContainer();
        base.setSize("100%", "100%");
        base.setLayout(layout);


        // main app body - scrolling okay
        // TODO: at some point we are going to have to split this
        // so this center panel has a north and south component
        centerPanel = new ContentPanel();
        centerPanel.setScrollMode(Style.Scroll.AUTO);
        centerPanel.setHeaderVisible(false);
        centerPanel.add(getMainPanel());
        centerPanel.setLayout(new FitLayout());
        centerPanel.layout();

        centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins());

        base.add(centerPanel, centerData);

        // left side nav - no scroll
        westPanel = new ContentPanel();
        westPanel.setLayout(new FitLayout());
        westPanel.setHeaderVisible(false);
        westPanel.setScrollMode(Style.Scroll.NONE);
        westPanel.layout();

        // left side is spit and collapsible
        westData = new BorderLayoutData(LayoutRegion.WEST, 200);
        westData.setSplit(true);
        westData.setCollapsible(true);
        westData.setMargins(new Margins());

        if (left) {
            leftMenu = new FlowPanel() {
                protected void onLoad() {

                    Element br = DOM.createElement("br");
                    DOM.setElementAttribute(br, "class", "clearit");
                    DOM.appendChild(DOM.getParent(this.getElement()), br);
                }
            };
            leftMenu.setStyleName("left-menu");
            leftMenuContainer = new FlowPanel() {

                protected void onLoad() {

                    Element br = DOM.createElement("br");
                    DOM.setElementAttribute(br, "class", "clearit");
                    DOM.appendChild(DOM.getParent(this.getElement()), br);
                }

            };
            leftMenuContainer.setStyleName("left-menu-container");

            leftMenu.add(leftMenuContainer);

            // add menus to the gxt containers
            westPanel.add(leftMenu);
            base.add(westPanel, westData);

        }
        initWidget(base);

        base.layout();

    }


    public void onShow(List<String> params) {
        if (firstShow) {
            firstShow = false;
            onFirstShow();
        }
        if (mainWidget instanceof AbstractComposite) {
            ((AbstractComposite) mainWidget).onShow(params);
        }

        base.layout();
        centerPanel.layout();
        westPanel.layout();

    }

    protected void onFirstShow() {

        topPanel = new FlowPanel();
        topPanel.setStyleName("top-panel");
    }


    public void addMenuItem(Widget widget) {
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
        if (centerPanel.findComponent(topPanel) == null) {
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


    public LayoutContainer getBase() {
        return base;
    }

    public void setBase(LayoutContainer base) {
        this.base = base;
    }

    public ContentPanel getWestPanel() {
        return westPanel;
    }

    public void setWest(ContentPanel westPanel) {
        this.westPanel = westPanel;
    }

    public ContentPanel getCenterPanel() {
        return centerPanel;
    }

    public void setCenter(ContentPanel centerPanel) {
        this.centerPanel = centerPanel;
    }


}
