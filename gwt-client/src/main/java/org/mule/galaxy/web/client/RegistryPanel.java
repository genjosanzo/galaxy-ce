package org.mule.galaxy.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
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

public class RegistryPanel extends AbstractMenuPanel {

    private Set artifactTypes;
    private Toolbox artifactTypesBox;
    private String workspaceId;
    private String workspaceName;
    private RegistryServiceAsync service;
    private WorkspacePanel workspacePanel;
    private Toolbox workspaceBox;
    
    public RegistryPanel(Galaxy galaxy) {
        super(galaxy);
        this.service = (RegistryServiceAsync) GWT.create(RegistryService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setServiceEntryPoint("/handler/registry.rpc");
        
        workspaceBox = new Toolbox();
        workspaceBox.setTitle("Workspaces");
        Image addImg = new Image("images/add_obj.gif");
        addImg.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new AddArtifactPanel());
            }
            
        });
        workspaceBox.addButton(addImg);
        
        Image addWkspcImg = new Image("images/adddir_wiz.png");
        addImg.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                setMain(new AddWorkspacePanel(workspaceId, workspaceName));
            }
            
        });
        
        workspaceBox.addButton(addWkspcImg);
        
        final Tree workspaceTree = new Tree();
        workspaceTree.setStyleName("workspaces");
        final TreeItem treeItem = workspaceTree.addItem("All");
        workspaceTree.setSelectedItem(treeItem);
        
        workspaceBox.add(workspaceTree);
        
        addMenuItem(workspaceBox);

        // Load the workspaces into a tree on the left
        service.getWorkspaces(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                Collection workspaces = (Collection) o;
                
                initWorkspaces(treeItem, workspaces);
                
                treeItem.setState(true);
            }
        });
        
        workspaceTree.addTreeListener(new TreeListener() {
            public void onTreeItemSelected(TreeItem ti) {
                workspaceName = ti.getText();
                setActiveWorkspace((String) ti.getUserObject());
            }

            public void onTreeItemStateChanged(TreeItem ti) {
            }
        });
        
        artifactTypesBox = new Toolbox();
        artifactTypesBox.setTitle("Artifact Types");
        addMenuItem(artifactTypesBox);
        
        initArtifactTypes();
        
        workspacePanel = new WorkspacePanel(this);
        setMain(workspacePanel);
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
                    artifactTypesBox.add(hl);
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
    }
    
    public void addArtifactTypeFilter(String id) {
        artifactTypes.add(id);
        workspacePanel.reloadArtifacts(workspaceId, artifactTypes);
    }

    public void removeArtifactTypeFilter(String id) {
        artifactTypes.remove(id);
        workspacePanel.reloadArtifacts(workspaceId, artifactTypes);
    }

    public void setActiveWorkspace(String workspaceId) {
        this.workspaceId = workspaceId;
        setMain(workspacePanel);
        workspacePanel.reloadArtifacts(workspaceId, artifactTypes);
    }

    public void refresh() {
        // TODO Auto-generated method stub
        
    }
}
