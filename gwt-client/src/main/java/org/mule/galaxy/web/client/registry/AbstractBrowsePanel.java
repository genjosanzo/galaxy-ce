package org.mule.galaxy.web.client.registry;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.util.ColumnView;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.client.workspace.WorkspaceForm;
import org.mule.galaxy.web.client.workspace.ManageWorkspacePanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WWorkspace;

/**
 * The basis for any form that lists out groups of artifacts.
 */
public abstract class AbstractBrowsePanel extends AbstractErrorShowingComposite {

    protected Set artifactTypes;
    protected Toolbox artifactTypesBox;
    protected RegistryServiceAsync service;
    protected ArtifactListPanel artifactListPanel;
    protected FlowPanel currentTopPanel;
    protected final Galaxy galaxy;
    protected MenuPanel menuPanel;
    private boolean first = true;
    
    public AbstractBrowsePanel(Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
        this.service = galaxy.getRegistryService();

        menuPanel = createRegistryMenuPanel();
        
        initWidget(menuPanel);
    }

    protected RegistryMenuPanel createRegistryMenuPanel() {
        return new RegistryMenuPanel(false, true);
    }
    
    public void onShow() {
        if (first) {
            artifactListPanel = new ArtifactListPanel(galaxy, this);
            
            artifactTypesBox = new Toolbox(false);
            artifactTypesBox.setTitle("By Artifact Type");
            
            initializeMenuAndTop();
            showArtifactTypes();
            first = false;
        }
        
        refresh();

        menuPanel.setTop(currentTopPanel);
    }

    protected void initializeMenuAndTop() {
    }

    public void refresh() {
        refreshArtifactTypes();
    }

    private void refreshArtifactTypes() {
        artifactTypes = new HashSet();
        artifactTypesBox.clear();
        artifactTypesBox.add(new Label("Loading..."));
        
        service.getArtifactTypes(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                artifactTypesBox.clear();
                Collection artifactTypes = (Collection) o;
                
                for (Iterator itr = artifactTypes.iterator(); itr.hasNext();) {
                    final WArtifactType at = (WArtifactType) itr.next();
                    
                    Hyperlink hl = new Hyperlink(at.getDescription(), "browse");
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
    
    public void addArtifactTypeFilter(String id) {
        artifactTypes.add(id);
        refreshArtifacts();
    }

    public void removeArtifactTypeFilter(String id) {
        artifactTypes.remove(id);
        refreshArtifacts();
    }

    public void refreshArtifacts() {
        menuPanel.setMain(artifactListPanel);
        artifactListPanel.reloadArtifacts();
    }

    public Set getArtifactTypes() {
        return artifactTypes;
    }
    
    public void showArtifactTypes() {
        menuPanel.addMenuItem(artifactTypesBox);
    }
    
    public void hideArtifactTypes() {
        menuPanel.removeMenuItem(artifactTypesBox);
    }

    // TODO
    protected int getErrorPanelPosition() {
        return 0;
    }
    
    // TODO: refactor ArtifactListPanel so these methods are not needed

    public String getWorkspaceId() {
        return "";
    }

    public Set getPredicates() {
        return new HashSet();
    }

    public String getFreeformQuery() {
        return null;
    }
}
