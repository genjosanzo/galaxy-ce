package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SearchPredicate;

public class SearchPanel
    extends Composite
{
    private FlowPanel panel;
    private Set rows;
    private Collection artifactIndexes;
    private Map artifactPropertyMap;
    private Button searchButton;
    private RegistryPanel registryPanel;
    private Hyperlink searchLink;
    private Button clearButton;
    private InlineFlowPanel buttonPanel;
    private Hyperlink freeformQueryLink;
    private TextArea freeformQueryArea;

    public SearchPanel(RegistryPanel rp) {
        registryPanel = rp;
        rows = new HashSet();
        
        panel = new FlowPanel();
        panel.setStyleName("search-panel");
        
        registryPanel.getRegistryService().getIndexes(new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initArtifactIndices((Collection) o);
            }
            
        });
        registryPanel.getRegistryService().getPropertyList(new AbstractCallback(registryPanel) {
            
            public void onSuccess(Object o) {
                initArtifactProperties((Map) o);
            }
        });

        freeformQueryArea = new TextArea();
        freeformQueryArea.setCharacterWidth(80);
        freeformQueryArea.setVisibleLines(7);
        freeformQueryLink = new Hyperlink("Freeform >>", "customQuery");
        freeformQueryLink.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                showHideFreeformQuery();
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
    
    public void initArtifactIndices(Collection c) {
        artifactIndexes = c;
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchPanelRow row = (SearchPanelRow) itr.next();
            row.addPropertySet("Indexes", artifactIndexes);
        }
    }
    
    public void initArtifactProperties(Map map) {
        artifactPropertyMap = map;
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchPanelRow row = (SearchPanelRow) itr.next();
            row.addPropertySet("Properties", artifactIndexes);
        }
    }
    
    public void addPredicate() {
        SearchPanelRow pred = new SearchPanelRow(this);
        if (artifactIndexes != null)
            pred.addPropertySet("Indexes", artifactIndexes);
        if (artifactPropertyMap != null)
            pred.addPropertySet("Properties", artifactPropertyMap);
        
        // Add the search button if we're adding our first row
        if (rows.size() == 0) {
            panel.add(freeformQueryLink);
            
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
            panel.remove(freeformQueryLink);
            panel.remove(freeformQueryArea);
            searchButton.click();
            panel.remove(buttonPanel);
            panel.insert(searchLink, 0);
        }
    }
    
    public void showHideFreeformQuery() {
        if (panel.getWidget(1) == freeformQueryArea) {
            panel.remove(freeformQueryArea);
            freeformQueryArea.setText("");
            freeformQueryLink.setText("Freeform >>");
        }
        else {
            panel.insert(freeformQueryArea, 1);
            freeformQueryArea.setText("Add a custom query...");
            freeformQueryArea.selectAll();
            freeformQueryLink.setText("Freeform <<");
        }
    }

    public Set getPredicates()
    {
        Set predicates = new HashSet();
        
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchPanelRow row = (SearchPanelRow) itr.next();
            SearchPredicate pred = row.getPredicate();
            if (pred != null)
                predicates.add(pred);
        }
        
        return predicates;
    }
}
