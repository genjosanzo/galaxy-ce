package org.mule.galaxy.web.client;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mule.galaxy.web.client.util.ColumnView;
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
    private int errorPosition = 1;
    protected ColumnView cv;
    private FlowPanel browsePanel;
    private FlowPanel searchingPanel;
    private SearchPanel searchPanel;
    
    public RegistryPanel(Galaxy galaxy) {
        super(galaxy);
        this.service = galaxy.getRegistryService();
        
        workspaceBox = new Toolbox(false);
        workspaceBox.setTitle("Workspaces");
        
        Image addImg = new Image("images/add_obj.gif");
        final RegistryPanel registryPanel = this;
        MenuPanelPageInfo page = new MenuPanelPageInfo("add-artifact", this) {
            public AbstractComposite createInstance() {
                return new ArtifactForm(registryPanel);
            }
        };
        addPage(page);
        addImg.addClickListener(createClickListener(page));
        
        Image addWkspcImg = new Image("images/fldr_obj.gif");
        page = new MenuPanelPageInfo("add-workspace", this) {
            public AbstractComposite createInstance() {
                return new EditWorkspacePanel(registryPanel, 
                                              workspaces,
                                              workspaceId);
            }
        };
        addPage(page);
        addWkspcImg.addClickListener(createClickListener(page));
        
        Image editWkspcImg = new Image("images/editor_area.gif");
        page = new MenuPanelPageInfo("edit-workspace-" + workspaceId, this) {
            public AbstractComposite createInstance() {
                return new EditWorkspacePanel(registryPanel, 
                                              workspaces,
                                              workspaceId);
            }
        };
        addPage(page);
        editWkspcImg.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                final TreeItem item = cv.getSelectedItem();
                TreeItem parent = item.getParentItem();
                final String parentId = parent != null ? (String) parent.getUserObject() : null;

                MenuPanelPageInfo page = new MenuPanelPageInfo("edit-workspace-" + workspaceId, registryPanel) {
                    public AbstractComposite createInstance() {
                        return new WorkspaceViewPanel(registryPanel, 
                                                      workspaces,
                                                      parentId,
                                                      workspaceId,
                                                      item.getText());
                    }
                };
                
                setMain(page);
                errorPosition = 0;
            }
            
        });
        
        FlowPanel browseToolbar = new FlowPanel();
        browseToolbar.setStyleName("toolbar");
        browseToolbar.add(addImg);
        browseToolbar.add(addWkspcImg);
        browseToolbar.add(editWkspcImg);
        
        Hyperlink searchLink = new Hyperlink("Search", "search");
        searchLink.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setTop(searchingPanel);
            }
        });
        browseToolbar.add(searchLink);
        
        //addMenuItem(workspaceBox);
        workspacePanel = new WorkspacePanel(registryPanel);
        
        searchingPanel = new FlowPanel();
        FlowPanel searchToolbar = new FlowPanel();
        searchToolbar.setStyleName("toolbar");
        Hyperlink browseLink = new Hyperlink("Browse Workspaces", "browse");
        browseLink.addClickListener(new ClickListener() {
            public void onClick(Widget w){
                setTop(browsePanel);
            }
        });
        searchToolbar.add(browseLink);
        searchingPanel.add(searchToolbar);
        searchPanel = new SearchPanel(this);
        searchingPanel.add(searchPanel);
        
        browsePanel = new FlowPanel(); 
        cv = new ColumnView();
        browsePanel.add(browseToolbar);
        browsePanel.add(cv);
        setTop(browsePanel);

        refreshWorkspaces();
        
        cv.addTreeListener(new TreeListener() {
            public void onTreeItemSelected(TreeItem ti) {
                setActiveWorkspace((String) ti.getUserObject());
            }

            public void onTreeItemStateChanged(TreeItem ti) {
            }
        });
        
        artifactTypesBox = new Toolbox(false);
        artifactTypesBox.setTitle("By Artifact Type");
        addMenuItem(artifactTypesBox);
        
        initArtifactTypes();
    }

    public void onShow() {
        super.onShow();
        refreshWorkspaces();
    }

    private ClickListener createClickListener(final MenuPanelPageInfo page) {
        return new ClickListener() {
            public void onClick(Widget w) {
                History.newItem(page.getName());
                errorPosition = 0;
            }
            
        };
    }

    public void refreshWorkspaces() {
        final TreeItem treeItem = new TreeItem();
        
        // Load the workspaces into a tree on the left
        service.getWorkspaces(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                workspaces = (Collection) o;
                
                initWorkspaces(treeItem, workspaces);

                TreeItem child = treeItem.getChild(0);
                workspaceId    = (String) child.getUserObject();
                
                cv.setRootItem(treeItem);
            }
        });
    }

    public RegistryServiceAsync getRegistryService() {
        return service;
    }

    private void initArtifactTypes() {
        artifactTypes = new HashSet();
        
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
            
            Collection children = wi.getWorkspaces();
            if (children != null) {
                initWorkspaces(treeItem, children);
            }
        }
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
    
    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    // TODO
    protected int getErrorPanelPosition() {
        return 0;
    }
}
