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
import java.util.List;
import java.util.Set;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.util.ColumnView;
import org.mule.galaxy.web.client.util.NavigationUtil;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.client.workspace.WorkspaceForm;
import org.mule.galaxy.web.client.workspace.ManageWorkspacePanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WWorkspace;

public class SearchPanel extends AbstractBrowsePanel {

    private FlowPanel searchPanel;
    private SearchForm searchForm;
    
    public SearchPanel(Galaxy galaxy) {
        super(galaxy);
    }

    protected RegistryMenuPanel createRegistryMenuPanel() {
        return new RegistryMenuPanel(true, false);
    }
    
    protected void initializeMenuAndTop() {
        FlowPanel browseToolbar = new FlowPanel();
        browseToolbar.setStyleName("toolbar");
        
        searchPanel = new FlowPanel(); 
        searchForm = new SearchForm(galaxy);
        searchPanel.add(searchForm);
        currentTopPanel = searchPanel;
        menuPanel.setTop(searchPanel);
    }

    public String getFreeformQuery() {
        return searchForm.getFreeformQuery();
    }

    public Set getPredicates() {
        return searchForm.getPredicates();
    }
    
    
}
