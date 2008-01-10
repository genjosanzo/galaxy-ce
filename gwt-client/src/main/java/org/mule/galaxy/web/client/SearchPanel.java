package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

public class SearchPanel
    extends Composite
{
    private FlowPanel panel;
    private Set rows;
    private Map artifactIndiceMap;
    private Map artifactPropertyMap;
    private Button searchButton;
    private RegistryPanel registryPanel;
    private Hyperlink searchLink;
    private Button clearButton;
    private InlineFlowPanel buttonPanel;

    public SearchPanel(RegistryPanel rp) {
        registryPanel = rp;
        rows = new HashSet();
        
        panel = new FlowPanel();
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

        
        searchLink = new Hyperlink("Search", "search");
        searchLink.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                panel.remove(searchLink);
                addPredicate();
              }
        });
        panel.add(searchLink);
        
        searchButton = new Button("Search", new ClickListener() {
           public void onClick(Widget sender) {
               registryPanel.reloadArtifacts();
           }
        });
        
        clearButton = new Button("Clear", new ClickListener() {
            public void onClick(Widget sender) {
                rows.clear();
                panel.clear();
                
                panel.insert(searchLink, 0);
                
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
            buttonPanel = new InlineFlowPanel();
            buttonPanel.setStyleName("search-button-panel");
            buttonPanel.add(searchButton);
            buttonPanel.add(clearButton);
            panel.add(buttonPanel);
        }
        
        panel.insert(pred, panel.getWidgetIndex(buttonPanel));
        rows.add(pred);
    }
    
    public void removePredicate(SearchPanelRow pred) {
        panel.remove(pred);
        rows.remove(pred);
        
        // Remove the search button if we're removing our last row
        if (rows.size() == 0) {
            searchButton.click();
            panel.remove(buttonPanel);
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
