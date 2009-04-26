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

package org.mule.galaxy.web.client.registry;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.LightBox;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.WArtifactView;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Forms the basis of any pages which do not list artifacts.
 */
public class RegistryMenuPanel extends MenuPanel {

    private final Galaxy galaxy;
    private ListBox viewBox;
    private String selectedViewId;
    private boolean first = true;
    private FlowPanel recentViewsPanel;
    private Toolbox menuLinks;
    private ItemInfo info;
    
    public RegistryMenuPanel(Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
    }

    public void onShow() {
        createLinks();
        loadViews();
    }

    private void createLinks() {
        if (!first) {
            return;
        }
        
        menuLinks = new Toolbox(false);
        addMenuItem(menuLinks, 0);

        Toolbox viewToolbox = new Toolbox(false);

        viewToolbox.add(asHorizontal(newLabel("View ", "toolbox-header"),
                                     new Hyperlink("New", "view/new"),
                                     new Label("...")));

        viewBox = new ListBox();
        viewBox.setStyleName("view-ListBox");
        viewBox.setSize("170", "");
        viewToolbox.add(viewBox);
        viewBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget arg0) {
                int idx = viewBox.getSelectedIndex();
                if (idx != -1) {
                    String id = viewBox.getValue(idx);

                    if (id.length() > 0) {
                        History.newItem("view/" + id);
                    }
                }
            }
        });

        addMenuItem(viewToolbox, 1);

        // recent view history
        /*
        recentViewsPanel = new FlowPanel();
        recentViewsPanel.setStyleName("recent-views");
        viewToolbox.add(recentViewsPanel);
        */

        first = false;
    }

    public void loadViews() {
        loadViews(null, null);
    }

    public void loadViews(String viewId, final AsyncCallback<WArtifactView> callback) {
        this.selectedViewId = viewId;
        galaxy.getRegistryService().getArtifactViews(new AbstractCallback(this) {

            public void onSuccess(Object views) {
                initializeViews((Collection) views, callback);
            }

        });

         
        galaxy.getRegistryService().getRecentArtifactViews(new AbstractCallback(this) {
             public void onSuccess(Object views) {
                 initializeRecentViews((Collection) views);
             }
         });   
    }

    protected void initializeRecentViews(Collection views) {
        recentViewsPanel.clear();
        for (Iterator itr = views.iterator(); itr.hasNext();) {
            WArtifactView wv = (WArtifactView) itr.next();

            recentViewsPanel.add(new Hyperlink(wv.getName(), "view/" + wv.getId()));
        }
    }

    protected void initializeViews(Collection views, AsyncCallback<WArtifactView> callback) {
        viewBox.clear();
        viewBox.addItem("Select...", "");
        for (Iterator itr = views.iterator(); itr.hasNext();) {
            WArtifactView wv = (WArtifactView) itr.next();

            viewBox.addItem(wv.getName(), wv.getId());

            if (wv.getId().equals(selectedViewId)) {
                viewBox.setSelectedIndex(viewBox.getItemCount() - 1);

                if (callback != null) {
                    callback.onSuccess(wv);
                }
            }
        }
    }

    public void setItem(final ItemInfo info) {
        this.info = info;
        
        menuLinks.clear();
        
        // add item

        if (info.isModifiable()) {
            Image addImg = new Image("images/add_obj.gif");
            addImg.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    w.addStyleName("gwt-Hyperlink");
                    
                    History.newItem("add-item/" + info.getId());
                }
            });
            
            Hyperlink addLink = new Hyperlink("New", "add-item/" + info.getId());
            menuLinks.add(asHorizontal(addImg, new Label(" "), addLink));
        }
        
        if (info.isDeletable()) {
            ClickListener cl = new ClickListener() {
                public void onClick(Widget arg0) {
                    warnDelete();
                }
            };
            Image img = new Image("images/delete_config.gif");
            img.setStyleName("icon-baseline");
            img.addClickListener(cl);
            Hyperlink hl = new Hyperlink("Delete", "artifact/" + info.getId());
            hl.addClickListener(cl);
            
            menuLinks.add(asHorizontal(img, new Label(" "), hl));
        }

        ClickListener cl = new ClickListener() {

            public void onClick(Widget sender) {
                Window.open(info.getArtifactFeedLink(), null, "scrollbars=yes");
            }
        };
        
        Image img = new Image("images/feed-icon.png");
//        img.setStyleName("feed-icon");
        img.setTitle("Versions Atom Feed");
        img.addClickListener(cl);
        img.setStyleName("icon-baseline");
        
        Hyperlink hl = new Hyperlink("Feed", "artifact-versions/" + info.getId());
        hl.addClickListener(cl);
        menuLinks.add(asHorizontal(img, new Label(" "), hl));

        // spacer to divide the actions
        SimplePanel spacer = new SimplePanel();
        spacer.addStyleName("hr");
        menuLinks.add(spacer);
    }

    protected void warnDelete()
    {
        new LightBox(new ConfirmDialog(new ConfirmDialogAdapter()
        {
            public void onConfirm()
            {
                galaxy.getRegistryService().delete(info.getId(), new AbstractCallback(RegistryMenuPanel.this)
                {
                    public void onSuccess(Object arg0)
                    {
                        galaxy.setMessageAndGoto("browse", "Item was deleted.");
                    }
                });
            }
        }, "Are you sure you want to delete this item?")).show();
    }
}
