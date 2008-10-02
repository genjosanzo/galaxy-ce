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
import org.mule.galaxy.web.client.util.WorkspaceOracle;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SearchPredicate;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasAlignment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SearchForm extends AbstractErrorShowingComposite {
    protected FlowPanel panel;
    private FlowPanel fieldPanel;
    private Set<SearchFormRow> rows;
    private Map<String, String> propertyDescriptors;
    private Button searchButton;
    private Button clearButton;
    private InlineFlowPanel buttonPanel;
    private Hyperlink freeformQueryLink;
    private TextArea freeformQueryArea;
    private final boolean allowFreeform;
    private SuggestBox workspaceTB;
    private CheckBox includeChildWkspcCB;
    private Galaxy galaxy;
    private FlexTable fieldTable;

    public SearchForm(Galaxy galaxy, String searchText, boolean allowFreeform) {
        this.galaxy = galaxy;
        this.allowFreeform = allowFreeform;
        rows = new HashSet<SearchFormRow>();

        panel = new FlowPanel();
        panel.setStyleName("search-panel");

        fieldPanel = new FlowPanel();

        galaxy.getRegistryService().getQueryProperties(new AbstractCallback<Map<String, String>>(this) {
            public void onSuccess(Map<String, String> o) {
                initQueryProperties(o);
            }
        });

        fieldTable = new FlexTable();

        initializeFields(fieldTable);
        fieldPanel.add(fieldTable);
        
        panel.add(fieldPanel);

        buttonPanel = new InlineFlowPanel();
        buttonPanel.setStyleName("search-button-panel");

        initializeButtons(buttonPanel, searchText);

        panel.add(buttonPanel);

        addPredicate();

        initWidget(panel);
    }

    protected void initializeFields(FlexTable table) {
        int row = table.getRowCount();
        table.setText(row, 0, "Workspace:");

        workspaceTB = new SuggestBox(new WorkspaceOracle(galaxy, this));
        table.setWidget(row, 1, workspaceTB);
        includeChildWkspcCB = new CheckBox();
        table.setText(row, 2, " Include Child Workspaces: ");
        table.getCellFormatter().setHorizontalAlignment(row, 2, HasAlignment.ALIGN_RIGHT);
        table.setWidget(row, 3, includeChildWkspcCB);
        table.getCellFormatter().setHorizontalAlignment(row, 3, HasAlignment.ALIGN_LEFT);

        freeformQueryArea = new TextArea();
        freeformQueryArea.setCharacterWidth(83);
        freeformQueryArea.setVisibleLines(7);
        freeformQueryLink = new Hyperlink("Use Freeform Query", galaxy.getCurrentToken());
        freeformQueryLink.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                showHideFreeformQuery();
            }
        });
    }

    protected void initializeButtons(FlowPanel buttonPanel, String searchText) {
        searchButton = new Button(searchText);

        clearButton = new Button("Clear", new ClickListener() {
            public void onClick(Widget sender) {
                clear();
                resetFieldPanel();
                freeformQueryArea.setText("");
                addPredicate();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            public void onClick(final Widget widget) {
                History.back();
            }
        });

        if (allowFreeform) {
            buttonPanel.add(freeformQueryLink);
        }
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancel);
    }

    public FlowPanel getPanel() {
        return panel;
    }

    public void clear() {
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

        fieldPanel.insert(pred, fieldPanel.getWidgetCount());
        rows.add(pred);
        return pred;
    }

    public void removePredicate(SearchFormRow pred) {
        fieldPanel.remove(pred);
        rows.remove(pred);

        // Add a new predicate if we're removing our last row
        if (rows.size() == 0) {
            addPredicate();
        }
    }

    public void showHideFreeformQuery() {
        if (fieldPanel.remove(freeformQueryArea)) {
            freeformQueryArea.setText("");
            freeformQueryLink.setText("Use Freeform Query");

            resetFieldPanel();
            addPredicate();
        } else {
            fieldPanel.clear();
            fieldPanel.insert(freeformQueryArea, 0);
            freeformQueryArea.setText("Add a custom query...");
            freeformQueryArea.selectAll();
            freeformQueryArea.setFocus(true);
            freeformQueryLink.setText("Use Structured Query");

            // Remove all the structured query rows
            rows.clear();
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
        return freeformQueryArea.getText();
    }

    public String getWorkspacePath() {
        return workspaceTB.getText();
    }

    public boolean isWorkspaceSearchRecursive() {
        return includeChildWkspcCB.isChecked();
    }

    public void setPredicates(Set<SearchPredicate> predicates) {
        rows.clear();
        resetFieldPanel();

        for (Iterator<SearchPredicate> itr = predicates.iterator(); itr.hasNext();) {
            SearchPredicate p = itr.next();

            SearchFormRow row = addPredicate();
            rows.add(row);
            row.setPredicate(p);
        }

        // Add an empty predicate to add more
        addPredicate();
    }

    protected void resetFieldPanel() {
        fieldPanel.clear();
        fieldPanel.add(fieldTable);
    }

    public void setWorkspace(String workspace) {
        workspaceTB.setText(workspace);
    }

    public void setWorkspaceSearchRecursive(boolean workspaceSearchRecursive) {
        includeChildWkspcCB.setChecked(workspaceSearchRecursive);
    }
}
