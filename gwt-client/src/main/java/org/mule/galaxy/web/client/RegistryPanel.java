package org.mule.galaxy.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RegistryPanel extends AbstractMenuPanel {

    private Set artifactTypes;
    private String currentWorkspace;
    private VerticalPanel artifactTypesPanel;
    private String workspaceId;
    private AddArtifactPanel addArtifactPanel;
    private RegistryServiceAsync service;
    private WorkspacePanel workspacePanel;
    
    public RegistryPanel(Galaxy galaxy) {
        super(galaxy);
        this.service = (RegistryServiceAsync) GWT.create(RegistryService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setServiceEntryPoint("/handler/registry.rpc");
        
        
        final Tree workspaceTree = new Tree();
        workspaceTree.setStyleName("workspaces");
        workspaceTree.addTreeListener(new TreeListener() {
            public void onTreeItemSelected(TreeItem ti) {
                setActiveWorkspace((String) ti.getUserObject());
            }

            public void onTreeItemStateChanged(TreeItem ti) {
            }
        });
        
        addMenuItem(workspaceTree);

        // Load the workspaces into a tree on the left
        service.getWorkspaces(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                Collection workspaces = (Collection) o;
                
                TreeItem treeItem = workspaceTree.addItem("Workspaces");
                initWorkspaces(treeItem, workspaces);
                
                treeItem.setState(true);
            }
        });

        artifactTypesPanel = new VerticalPanel();
        addMenuItem(artifactTypesPanel);
        
        Label label = new Label("Artifact Types");
        label.setStyleName("left-menu-header");
        artifactTypesPanel.add(label);
        artifactTypesPanel.setStyleName("artifactTypesPanel");
        
        initArtifactTypes();
        
        addArtifactPanel = new AddArtifactPanel();
        //setRightPanel(addArtifactPanel);
        
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
                    artifactTypesPanel.add(hl);
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
        setMessage(new Label("Artifact filter " + id));
        artifactTypes.add(id);
        workspacePanel.reloadArtifacts(workspaceId, artifactTypes);
    }

    public void removeArtifactTypeFilter(String id) {
        setMessage(new Label("Removed artifact filter " + id));
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
