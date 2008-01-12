package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.client.workspace.EditWorkspacePanel;
import org.mule.galaxy.web.client.workspace.WorkspaceViewPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WWorkspace;

public class RegistryPanel extends AbstractMenuPanel {

    private Set artifactTypes;
    private Toolbox artifactTypesBox;
    private String workspaceId;
    private Collection workspaces;
    private RegistryServiceAsync service;
    private WorkspacePanel workspacePanel;
    private Toolbox workspaceBox;
    private Tree workspaceTree;
    private int errorPosition = 1;
    
    public RegistryPanel(Galaxy galaxy) {
        super(galaxy);
        this.service = galaxy.getRegistryService();
        
        workspaceBox = new Toolbox(false);
        workspaceBox.setTitle("Workspaces");
        
        Image addImg = new Image("images/add_obj.gif");
        final RegistryPanel registryPanel = this;
        addImg.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new ArtifactForm(registryPanel));
                errorPosition = 0;
            }
            
        });
        workspaceBox.addButton(addImg);
        
        Image addWkspcImg = new Image("images/fldr_obj.gif");
        addWkspcImg.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                setMain(new EditWorkspacePanel(registryPanel, 
                                               workspaces,
                                               workspaceId));
                errorPosition = 0;
            }
            
        });
        workspaceBox.addButton(addWkspcImg);
        
        Image editWkspcImg = new Image("images/editor_area.gif");
        editWkspcImg.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                TreeItem item = workspaceTree.getSelectedItem();
                TreeItem parent = item.getParentItem();
                String parentId = null;
                if (parent != null) {
                    parentId = (String) parent.getUserObject();
                }
                setMain(new WorkspaceViewPanel(registryPanel, 
                                               workspaces,
                                               parentId,
                                               workspaceId,
                                               item.getText()));
                errorPosition = 0;
            }
            
        });
        workspaceBox.addButton(editWkspcImg);
        this.workspaceTree = new Tree();
        workspaceTree.setStyleName("workspaces");
        workspaceBox.add(workspaceTree);
        
        addMenuItem(workspaceBox);

        refreshWorkspaces();
        
        workspaceTree.addTreeListener(new TreeListener() {
            public void onTreeItemSelected(TreeItem ti) {
                setActiveWorkspace((String) ti.getUserObject());
            }

            public void onTreeItemStateChanged(TreeItem ti) {
            }
        });
        
        artifactTypesBox = new Toolbox(false);
        artifactTypesBox.setTitle("Artifact Types");
        addMenuItem(artifactTypesBox);
        
        initArtifactTypes();
    }

    public void refreshWorkspaces() {
        if (workspaceTree.getItemCount() > 0) {
            workspaceTree.clear();
        }
        
        final TreeItem treeItem = workspaceTree.addItem("All");
        treeItem.setState(true);
        
        // Load the workspaces into a tree on the left
        service.getWorkspaces(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                workspaces = (Collection) o;
                
                initWorkspaces(treeItem, workspaces);
            }
        });
    }

    public RegistryServiceAsync getRegistryService() {
        return service;
    }

    private void initArtifactTypes() {
        artifactTypes = new HashSet();
        
        // Load the workspaces into a tree on the left
        service.getArtifactTypes(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                Collection workspaces = (Collection) o;
                
                for (Iterator itr = workspaces.iterator(); itr.hasNext();) {
                    final WArtifactType at = (WArtifactType) itr.next();
                    
                    Hyperlink hl = new Hyperlink(at.getDescription(), at.getId());
                    hl.addClickListener(new ClickListener() {
                        public void onClick(Widget w) {
                            String style = w.getStyleName();
                            if ("unselected-link".equals(style)) {
                                w.setStyleName("selected-link");
                                addArtifactTypeFilter(at.getId());
                            } else {
                                w.setStyleName("unselected-link");
                                removeArtifactTypeFilter(at.getId());
                            }
                            
                        }
                    });
                    hl.setStyleName("unselected-link");
                    artifactTypesBox.add(hl, false);
                }
            }
        });
    }
    
    private void initWorkspaces(TreeItem ti, Collection workspaces) {
        for (Iterator itr = workspaces.iterator(); itr.hasNext();) {
            WWorkspace wi = (WWorkspace) itr.next();
            
            TreeItem treeItem = ti.addItem(wi.getName());
            treeItem.setUserObject(wi.getId());
            
            Collection workspaces2 = wi.getWorkspaces();
            if (workspaces2 != null) {
                initWorkspaces(treeItem, workspaces2);
            }
        }
        
        ti.setState(true);
        TreeItem child = ti.getChild(0);
        workspaceTree.setSelectedItem(child, false);
        
        workspaceId = (String) child.getUserObject();
        
        workspacePanel = new WorkspacePanel(this);
        setMain(workspacePanel);
    }
    
    public void addArtifactTypeFilter(String id) {
        artifactTypes.add(id);
        reloadArtifacts();
    }

    public void removeArtifactTypeFilter(String id) {
        artifactTypes.remove(id);
        reloadArtifacts();
    }

    public void setActiveWorkspace(String workspaceId) {
        this.workspaceId = workspaceId;
        reloadArtifacts();
    }
    
    public void reloadArtifacts() {
        setMain(workspacePanel);
        errorPosition = 1;
        workspacePanel.reloadArtifacts();
    }

    public Set getArtifactTypes() {
        return artifactTypes;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public Collection getWorkspaces() {
        return workspaces;
    }


    protected int getErrorPanelPosition() {
        return errorPosition;
    }
}
