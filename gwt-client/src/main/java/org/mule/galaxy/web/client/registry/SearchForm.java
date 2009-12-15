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

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.ItemPathOracle;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SearchPredicate;

public class SearchForm extends AbstractErrorShowingComposite {
    private FlowPanel panel;
    protected FlowPanel searchFieldPanel;
    private Set<SearchFormRow> rows;
    private Map<String, String> propertyDescriptors;
    private Button searchButton;
    private Button clearButton;
    private InlineFlowPanel buttonPanel;
    private TextArea freeformQueryArea;
    private SuggestBox workspaceTB;
    private CheckBox includeChildWkspcCB;
    private Galaxy galaxy;
    private boolean freeform = false;
    private Hyperlink freeformQueryLink;
    private int topRows;
    private FlexTable searchTable;
    private final String searchText;
    private boolean freeformQuerySet;
    
    public SearchForm(Galaxy galaxy, String searchText) {
        this(galaxy, searchText, true);
    }
    
    protected SearchForm(Galaxy galaxy, String searchText, boolean init) {
        this.galaxy = galaxy;
        this.searchText = searchText;
        rows = new HashSet<SearchFormRow>();

        panel = new FlowPanel();
        panel.setStyleName("search-panel");

        // where we put the structured search stuff
        searchFieldPanel = new FlowPanel();
        panel.add(searchFieldPanel);
        if (init) {
            initialize();
        }
        
        initWidget(panel);
    }

    protected void initialize() {
        galaxy.getRegistryService().getQueryProperties(new AbstractCallback<Map<String, String>>(this) {
            public void onSuccess(Map<String, String> o) {
                initQueryProperties(o);
            }
        });

        searchTable = new FlexTable();
        
        initializeTopInfo(searchTable);
        searchFieldPanel.add(searchTable);
        
        // Store the # of rows used so we can clear everything below
        // when we switch to freeform view
        topRows = searchTable.getRowCount();

        freeformQueryArea = new TextArea();
        freeformQueryArea.setCharacterWidth(83);
        freeformQueryArea.setVisibleLines(7);
        freeformQueryArea.selectAll();
        freeformQueryArea.setFocus(true);
        freeformQueryArea.setText("Add a custom query...");
        freeformQueryArea.addChangeListener(new ChangeListener() {

            public void onChange(Widget arg0) {
                freeformQuerySet = true;
            }
        });
        switchForm(freeform);      

        // Search, clear, etc buttons
        buttonPanel = new InlineFlowPanel();
        buttonPanel.setStyleName("search-button-panel");
        
        initializeButtons(buttonPanel, searchText);
        panel.add(buttonPanel);
    }
    
    protected void switchForm(boolean freeform) {
        if (!freeform) {
            searchFieldPanel.remove(freeformQueryArea);
            initializeStructuredSearchFields(searchTable);
            
            for (SearchFormRow row : rows) {
                searchFieldPanel.add(row);
            }
        } else {
            while (topRows < searchTable.getRowCount()) {
                searchTable.removeRow(searchTable.getRowCount()-1);
            }
            searchFieldPanel.add(freeformQueryArea);
            
            removeSearchFormRows();
        }

        this.freeform = freeform;
    }

    private void removeSearchFormRows() {
        for (SearchFormRow row : rows) {
            searchFieldPanel.remove(row);
        }
    }
    

    /**
     * This is used for views to add a view name and configure whether or not the view is shared.
     * @param table
     */
    protected void initializeTopInfo(FlexTable table) {
    }
    
    protected void initializeStructuredSearchFields(FlexTable table) {
        int row = table.getRowCount();
        table.setText(row, 0, "Workspace:");

        workspaceTB = new SuggestBox(new ItemPathOracle(galaxy, this));
        table.setWidget(row, 1, workspaceTB);
        includeChildWkspcCB = new CheckBox();
        table.setText(row, 2, " Include Child Workspaces: ");
        table.getCellFormatter().setHorizontalAlignment(row, 2, HasAlignment.ALIGN_RIGHT);
        table.setWidget(row, 3, includeChildWkspcCB);
        table.getCellFormatter().setHorizontalAlignment(row, 3, HasAlignment.ALIGN_LEFT);
    }

    protected void initializeButtons(FlowPanel buttonPanel, String searchText) {
        searchButton = new Button(searchText);

        clearButton = new Button("Clear", new ClickListener() {
            public void onClick(Widget sender) {
                if (freeform) {
                    freeformQueryArea.setText("");
                } else {
                    clear();
                    addPredicate();
                }
            }
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            public void onClick(final Widget widget) {
                History.back();
            }
        });

        ClickListener switchListener = new ClickListener() {
            public void onClick(Widget sender) {
                switchForm(!freeform);
                if (freeform) {
                    freeformQueryLink.setText("Use Structured Query");
                } else {
                    freeformQueryLink.setText("Use Freeform Query");
                }
            }
        };
        
        freeformQueryLink = new Hyperlink("Use Freeform Query", History.getToken());
        freeformQueryLink.addClickListener(switchListener);
        buttonPanel.add(freeformQueryLink);
         
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancel);
    }

    public FlowPanel getPanel() {
        return panel;
    }

    public void clear() {
        for (Iterator<SearchFormRow> itr = rows.iterator(); itr.hasNext();) {
            SearchFormRow row = itr.next();
            searchFieldPanel.remove(row);
        }
        rows.clear();
    }

    public void addSearchListener(ClickListener listener) {
        searchButton.addClickListener(listener);
    }

    public void initQueryProperties(Map<String, String> props) {
        propertyDescriptors = props;
        for (Iterator<SearchFormRow> itr = rows.iterator(); itr.hasNext();) {
            SearchFormRow row = itr.next();
            row.addPropertySet("----", propertyDescriptors);
        }
    }

    public SearchFormRow addPredicate() {
        SearchFormRow pred = new SearchFormRow(this);
        if (propertyDescriptors != null)
            pred.addPropertySet("Properties:", propertyDescriptors);

        searchFieldPanel.insert(pred, searchFieldPanel.getWidgetCount());
        rows.add(pred);
        return pred;
    }

    public void removePredicate(SearchFormRow pred) {
        searchFieldPanel.remove(pred);
        rows.remove(pred);

        // Add a new predicate if we're removing our last row
        if (rows.size() == 0) {
            addPredicate();
        }
    }

    public Set<SearchPredicate> getPredicates() {
        Set<SearchPredicate> predicates = new HashSet<SearchPredicate>();

        for (Iterator<SearchFormRow> itr = rows.iterator(); itr.hasNext();) {
            SearchFormRow row = itr.next();
            SearchPredicate pred = row.getPredicate();
            if (pred != null)
                predicates.add(pred);
        }

        return predicates;
    }

    public String getFreeformQuery() {
        if (!freeformQuerySet || !freeform) {
            return "";
        }
        return freeformQueryArea.getText();
    }

    public void setFreeformQuery(String queryString) {
        freeformQueryArea.setText(queryString);
        switchForm(true);
    }

    public String getWorkspacePath() {
        return workspaceTB.getText();
    }

    public boolean isWorkspaceSearchRecursive() {
        return includeChildWkspcCB.isChecked();
    }

    public void setPredicates(Set<SearchPredicate> predicates) {
        removeSearchFormRows();
        
        if (predicates != null) {
            for (Iterator<SearchPredicate> itr = predicates.iterator(); itr.hasNext();) {
                SearchPredicate p = itr.next();
    
                SearchFormRow row = addPredicate();
                rows.add(row);
                row.setPredicate(p);
            }       
        }
        // a blank row to add more search terms
        addPredicate();
    }

    public void setWorkspace(String workspace) {
        workspaceTB.setText(workspace);
    }

    public void setWorkspaceSearchRecursive(boolean workspaceSearchRecursive) {
        includeChildWkspcCB.setChecked(workspaceSearchRecursive);
    }
}
