/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.client.registry;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SearchPredicate;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SearchForm
    extends AbstractErrorShowingComposite
{
    private FlowPanel panel;
    private Set rows;
    private Map artifactPropertyMap;
    private Button searchButton;
    private Button clearButton;
    private InlineFlowPanel buttonPanel;
    private Hyperlink freeformQueryLink;
    private TextArea freeformQueryArea;

    public SearchForm(Galaxy galaxy) {
        rows = new HashSet();
        
        panel = new FlowPanel();
        panel.setStyleName("search-panel");
        
        galaxy.getRegistryService().getPropertyList(new AbstractCallback(this) {
            
            public void onSuccess(Object o) {
                initArtifactProperties((Map) o);
            }
        });

        freeformQueryArea = new TextArea();
        freeformQueryArea.setCharacterWidth(83);
        freeformQueryArea.setVisibleLines(7);
        freeformQueryLink = new Hyperlink("Use Freeform Query", "customQuery");
        freeformQueryLink.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                showHideFreeformQuery();
            }
        });
        
        searchButton = new Button("Search");
        
        clearButton = new Button("Clear", new ClickListener() {
            public void onClick(Widget sender) {
                clear();
                panel.clear();
                freeformQueryArea.setText("");
                
                addPredicate();
            }
         });
        
        addPredicate();
        
        initWidget(panel);
    }

    public void clear() {
        rows.clear();
    }

    public void addSearchListener(ClickListener listener) {
        searchButton.addClickListener(listener);
    }
    
    public void initArtifactProperties(Map map) {
        artifactPropertyMap = map;
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchFormRow row = (SearchFormRow) itr.next();
            row.addPropertySet("----", artifactPropertyMap);
        }
    }
    
    public void addPredicate() {
        SearchFormRow pred = new SearchFormRow(this);
        if (artifactPropertyMap != null)
            pred.addPropertySet("Properties", artifactPropertyMap);
        
        // Add the search button if we're adding our first row
        if (rows.size() == 0) {
            buttonPanel = new InlineFlowPanel();
            buttonPanel.setStyleName("search-button-panel");
            buttonPanel.add(freeformQueryLink);
            buttonPanel.add(searchButton);
            buttonPanel.add(clearButton);
            panel.add(buttonPanel);
        }
        
        panel.insert(pred, panel.getWidgetIndex(buttonPanel));
        rows.add(pred);
    }
    
    public void removePredicate(SearchFormRow pred) {
        panel.remove(pred);
        rows.remove(pred);
        
        // Add a new predicate if we're removing our last row
        if (rows.size() == 0) {
            addPredicate();
        }
    }
    
    public void showHideFreeformQuery() {
        if (panel.remove(freeformQueryArea)) {
            freeformQueryArea.setText("");
            freeformQueryLink.setText("Use Freeform Query");
            
            // Clear the panel because addPredicate will add everything back
            panel.clear();
            addPredicate();
        }
        else {
            panel.insert(freeformQueryArea, 0);
            freeformQueryArea.setText("Add a custom query...");
            freeformQueryArea.selectAll();
            freeformQueryArea.setFocus(true);
            freeformQueryLink.setText("Use Structured Query");
            
            // Remove all the structured query rows
            for (Iterator iter=rows.iterator(); iter.hasNext();)
                panel.remove((Widget) iter.next());
            rows.clear();
        }
    }

    public Set getPredicates()
    {
        Set predicates = new HashSet();
        
        for (Iterator itr = rows.iterator(); itr.hasNext();) {
            SearchFormRow row = (SearchFormRow) itr.next();
            SearchPredicate pred = row.getPredicate();
            if (pred != null)
                predicates.add(pred);
        }
        
        return predicates;
    }

    public String getFreeformQuery()
    {
        return freeformQueryArea.getText();
    }
}
