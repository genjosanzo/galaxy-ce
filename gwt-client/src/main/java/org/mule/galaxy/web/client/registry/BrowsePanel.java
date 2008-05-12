package org.mule.galaxy.web.client.registry;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.ColumnView;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.NavigationUtil;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WWorkspace;

public class BrowsePanel extends AbstractBrowsePanel {

    private static String lastWorkspaceId;
    private String workspaceId;
    private Collection workspaces;
    protected ColumnView cv;
    protected TreeItem workspaceTreeItem;
    private FlowPanel browsePanel;
    private SimplePanel manageWorkspacePanel;
    private Image manageWkspcImg;
    private Hyperlink manageWkspcLink;
    
    public BrowsePanel(Galaxy galaxy) {
        super(galaxy);
    }

    public void onShow(List params) {
        if (params.size() > 0) {
            workspaceId = (String) params.get(0);
        }
        
        super.onShow(params);
    }

    protected RegistryMenuPanel createRegistryMenuPanel() {
        return new RegistryMenuPanel(false, true) {

            protected void addOtherLinks(Toolbox topMenuLinks) {
                manageWorkspacePanel = new SimplePanel();
                topMenuLinks.add(manageWorkspacePanel);
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

    private InlineFlowPanel createManageWorkspaceLinks() {
        manageWkspcImg = new Image("images/editor_area.gif");
        String token = "manage-workspace";
        
        manageWkspcLink = new Hyperlink("Manage Workspace", "");
        InlineFlowPanel manageLinks = asHorizontal(manageWkspcImg, new Label(" "), manageWkspcLink);
        return manageLinks;
    }
    
    public void refresh() {
        refreshWorkspaces();
        
        manageWorkspacePanel.clear();
        manageWorkspacePanel.add(createManageWorkspaceLinks());
        
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
                
                String token = "manage-workspace/" + workspaceId;
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
