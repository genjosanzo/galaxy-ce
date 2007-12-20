package org.mule.galaxy.web.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegistryPanel extends Composite {

    private List artifactTypes;
    private String currentWorkspace;
    private VerticalPanel mainPanel;
    private VerticalPanel artifactTypesPanel;
    private String workspaceId;
    private AddArtifactPanel addArtifactPanel;
    private RegistryServiceAsync service;
    private DockPanel repoPanel;
    private WorkspacePanel workspacePanel;
    
    public RegistryPanel(RegistryServiceAsync service) {
        super();
        this.service = service;
        
        repoPanel = new DockPanel();
        
        final Tree workspaceTree = new Tree();
        workspaceTree.addTreeListener(new TreeListener() {
            public void onTreeItemSelected(TreeItem ti) {
                setActiveWorkspace((String) ti.getUserObject());
            }


            public void onTreeItemStateChanged(TreeItem ti) {
            }
        });
        
        VerticalPanel leftMenu = new VerticalPanel();
        leftMenu.add(workspaceTree);
        leftMenu.setStyleName("left-menu");
        
        repoPanel.add(leftMenu, DockPanel.WEST);
        
        // Load the workspaces into a tree on the left
        service.getWorkspaces(new AsyncCallback() {
            public void onFailure(Throwable arg0) {
            }

            public void onSuccess(Object o) {
                Collection workspaces = (Collection) o;
                
                TreeItem treeItem = workspaceTree.addItem("Workspaces");
                initWorkspaces(treeItem, workspaces);
                
                treeItem.setState(true);
            }
        });

        artifactTypesPanel = new VerticalPanel();
        leftMenu.add(artifactTypesPanel);
        
        Label label = new Label("Artifact Types");
        label.setStyleName("left-menu-header");
        artifactTypesPanel.add(label);
        artifactTypesPanel.setStyleName("artifactTypesPanel");
        
        initArtifactTypes();
        
        mainPanel = new VerticalPanel();
        mainPanel.setWidth("100%");
        mainPanel.setStyleName("main-panel");
        repoPanel.add(mainPanel, DockPanel.CENTER);
        repoPanel.setCellWidth(mainPanel, "100%");
        
        addArtifactPanel = new AddArtifactPanel();
        //setRightPanel(addArtifactPanel);
        
        workspacePanel = new WorkspacePanel();
        mainPanel.add(workspacePanel);
        
        initWidget(repoPanel);
    }

    private void initArtifactTypes() {
        // Load the workspaces into a tree on the left
        service.getArtifactTypes(new AsyncCallback() {

            public void onFailure(Throwable t) {
                //t.printStackTrace();
                //mainPanel.add(new Label("message: " + t.getMessage()));
                
            }

            public void onSuccess(Object o) {
                Collection workspaces = (Collection) o;
                mainPanel.add(new Label("success: " + o));
                
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
        mainPanel.add(new Label("Artifact filter " + id));
    }
    
    public void removeArtifactTypeFilter(String id) {
        mainPanel.add(new Label("Removed artifact filter " + id));
    }

    public void setActiveWorkspace(String userObject) {
        this.workspaceId = userObject;
        refresh();
        mainPanel.add(new Label("Active workspace set to " + userObject));
    }

    public void refresh() {
        // TODO Auto-generated method stub
        
    }
}
