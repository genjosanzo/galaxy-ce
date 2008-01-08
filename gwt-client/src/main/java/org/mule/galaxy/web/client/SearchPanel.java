package org.mule.galaxy.web.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel
    extends Composite
{
    private VerticalPanel panel;
    private Set rows;
    private Map artifactIndiceMap;
    private Map artifactPropertyMap;
    private Button searchButton;
    private RegistryPanel registryPanel;

    public SearchPanel(RegistryPanel rp) {
        registryPanel = rp;
        rows = new HashSet();
        
        panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.setStyleName("search-panel");
        
        registryPanel.getRegistryService().getIndexes(new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initArtifactIndices((Map) o);
            }
            
        });
        registryPanel.getRegistryService().getPropertyList(new AbstractCallback(registryPanel) {
            
            public void onSuccess(Object o) {
                initArtifactProperties((Map) o);
            }
        });

        
        //
        // Set up the title bar
        //
        DockPanel titleBar = new DockPanel();
        titleBar.setWidth("100%");
        
        Label label = new Label("Search");
        titleBar.add(label, DockPanel.WEST);
        titleBar.setCellWidth(label, "100%");
        
        Button button = new Button("+", new ClickListener() {
            public void onClick(Widget sender) {
                addPredicate();
              }
            });
        button.setWidth("20px");
        titleBar.add(button, DockPanel.EAST);
        
        panel.add(titleBar);
        
        
        searchButton = new Button("Search", new ClickListener() {
           public void onClick(Widget sender) {
               registryPanel.reloadArtifacts();
           }
        });
        
        initWidget(panel);
    }
    
    public void initArtifactIndices(Map map) {
        artifactIndiceMap = map;
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchPanelRow row = (SearchPanelRow) itr.next();
            row.addProperties(artifactIndiceMap);
        }
    }
    
    public void initArtifactProperties(Map map) {
        artifactPropertyMap = map;
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchPanelRow row = (SearchPanelRow) itr.next();
            row.addProperties(artifactPropertyMap);
        }
    }
    
    public void addPredicate() {
        SearchPanelRow pred = new SearchPanelRow(this);
        if (artifactIndiceMap != null)
            pred.addProperties(artifactIndiceMap);
        if (artifactPropertyMap != null)
            pred.addProperties(artifactPropertyMap);
        
        // Add the search button if we're adding our first row
        if (rows.size() == 0) {
            panel.add(searchButton);
            panel.setCellHorizontalAlignment(searchButton, HasAlignment.ALIGN_RIGHT);
        }
        
        panel.insert(pred, panel.getWidgetIndex(searchButton));
        rows.add(pred);
    }
    
    public void removePredicate(SearchPanelRow pred) {
        panel.remove(pred);
        rows.remove(pred);
        
        // Remove the search button if we're removing our last row
        if (rows.size() == 0) {
            searchButton.click();
            panel.remove(searchButton);
        }
    }

    public Set getPredicates()
    {
        Set predicates = new HashSet();
        
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchPanelRow row = (SearchPanelRow) itr.next();
            predicates.add(row.getPredicate());
        }
        
        return predicates;
    }
}
