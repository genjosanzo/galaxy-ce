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

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.ColumnView;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.NavigationUtil;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WWorkspace;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class BrowsePanel extends AbstractBrowsePanel {

    private static String lastWorkspaceId;
    private String workspaceId;
    private Collection workspaces;
    protected ColumnView cv;
    protected TreeItem workspaceTreeItem;
    private FlowPanel browsePanel;
    private Image manageWkspcImg;
    private Hyperlink manageWkspcLink;
    
    public BrowsePanel(Galaxy galaxy) {
        super(galaxy);
    }

    protected String getHistoryToken() {
        if (workspaceId != null) {
            return "browse_" + workspaceId;
        } else {
            return "browse";
        }
    }
    
    public void onShow(List params) {
        if (params.size() > 0) {
            workspaceId = (String) params.get(0);
        }
        
        super.onShow(params);
    }

    protected RegistryMenuPanel createRegistryMenuPanel() {
        return new RegistryMenuPanel(galaxy, false, true) {

            protected void addBottomLinks(Toolbox topMenuLinks) {
                manageWkspcImg = new Image("images/editor_area.gif");
                
                manageWkspcLink = new Hyperlink("Manage Workspace", "");
                
                InlineFlowPanel manageLinks = asHorizontal(manageWkspcImg, new Label(" "), manageWkspcLink);
                
                topMenuLinks.add(manageLinks);
            }
        };
    }

    protected void initializeMenuAndTop() {
        FlowPanel browseToolbar = new FlowPanel();
        browseToolbar.setStyleName("toolbar");
        
        browsePanel = new FlowPanel(); 
        cv = new ColumnView();
        browsePanel.add(browseToolbar);
        browsePanel.add(cv);
        currentTopPanel = browsePanel;
        menuPanel.setTop(browsePanel);
        
        cv.addTreeListener(new TreeListener() {
            public void onTreeItemSelected(TreeItem ti) {
                setActiveWorkspace((String) ti.getUserObject());
            }

            public void onTreeItemStateChanged(TreeItem ti) {
            }
        });
    }

    public void refresh() {
        refreshWorkspaces();
        
        super.refresh();
    }
    
    public void refreshWorkspaces() {
        final TreeItem treeItem = new TreeItem();
        
        // Load the workspaces into a tree on the left
        service.getWorkspaces(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                workspaces = (Collection) o;
                
                initWorkspaces(treeItem, workspaces);

                if (workspaceId == null) {
                    TreeItem child = treeItem.getChild(0);
                    workspaceTreeItem = child;
                    
                    setActiveWorkspace((String) child.getUserObject());
                }
                cv.setRootItem(treeItem, workspaceTreeItem);
                
                String token = "manage-workspace_" + workspaceId;
                manageWkspcImg.addClickListener(NavigationUtil.createNavigatingClickListener(token));
                manageWkspcLink.setTargetHistoryToken(token);
            }
        });
    }
    
    private void initWorkspaces(TreeItem ti, Collection workspaces) {
        for (Iterator itr = workspaces.iterator(); itr.hasNext();) {
            WWorkspace wi = (WWorkspace) itr.next();
            
            TreeItem treeItem = ti.addItem(wi.getName());
            treeItem.setUserObject(wi.getId());
            
            if (workspaceId != null && workspaceId.equals(wi.getId())) {
                setActiveWorkspace(workspaceId);
                workspaceTreeItem = treeItem;
            }
            
            Collection children = wi.getWorkspaces();
            if (children != null) {
                initWorkspaces(treeItem, children);
            }
        }
    }

    protected WWorkspace getWorkspace(String workspaceId) {
        return getWorkspace(workspaceId, workspaces);
    }

    private WWorkspace getWorkspace(String workspaceId2, Collection workspaces2) {
        if (workspaces2 == null) return null;
        
        for (Iterator itr = workspaces2.iterator(); itr.hasNext();) {
            WWorkspace w = (WWorkspace)itr.next();
            
            if (w.getId().equals(workspaceId2)) {
                return w;
            }
            
            WWorkspace child = getWorkspace(workspaceId2, w.getWorkspaces());
            if (child != null) return child;
        }
        return null;
    }
    
    public void setActiveWorkspace(String workspaceId) {
        BrowsePanel.lastWorkspaceId = workspaceId;
        this.workspaceId = workspaceId;
        refreshArtifacts();
    }

    protected void fetchArtifacts(int resultStart, int maxResults, AbstractCallback callback) {
        galaxy.getRegistryService().getArtifacts(workspaceId, null, false, 
                                                 getAppliedArtifactTypeFilters(), 
                                                 new HashSet(), null, 
                                                 resultStart, maxResults, callback);
    }
    
    public String getWorkspaceId() {
        return workspaceId;
    }
    
    public static String getLastWorkspaceId() {
        return lastWorkspaceId;
    }

    public Collection getWorkspaces() {
        return workspaces;
    }
}
