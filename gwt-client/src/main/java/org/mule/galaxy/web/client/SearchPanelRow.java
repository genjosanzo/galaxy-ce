package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.mule.galaxy.web.rpc.SearchPredicate;
import org.mule.galaxy.web.rpc.WIndex;

public class SearchPanelRow
    extends Composite
{
    private SearchPanel searchPanel;
    private ListBox     propertyList;
    private HorizontalPanel contents;
    private ListBox matchTypeList;
    private TextBox valueTextBox;
    
    public SearchPanelRow(SearchPanel sp) {
        super();
        
        searchPanel = sp;
        
        DockPanel dock = new DockPanel();
        dock.setStylePrimaryName("search-predicate");
        dock.setWidth("100%");
        
        propertyList = new ListBox();
        propertyList.setWidth("175px");
        propertyList.addItem("Content Type", "contentType");
        propertyList.addItem("Description", "documentType");
        propertyList.addItem("Document Type", "documentType");
        propertyList.addItem("Name", "name");
        propertyList.addItem("Phase", "phase");
        
        propertyList.setSelectedIndex(3);
        propertyList.addChangeListener(new ChangeListener() {
           public void onChange(Widget sender) {
               processTypeChange();
           }
        });
        dock.add(propertyList, DockPanel.WEST);
        
        contents = new HorizontalPanel();
        contents.setWidth("100%");
        dock.add(contents, DockPanel.CENTER);
        dock.setCellWidth(contents, "100%");
        processTypeChange();
        
        HorizontalPanel buttons = new HorizontalPanel();
        final SearchPanelRow pred = this;
        Button del = new Button("-", new ClickListener() {
            public void onClick(Widget sender) {
                searchPanel.removePredicate(pred);
              }
            });
        del.setWidth("20px");
        buttons.add(del);
        Button add = new Button("+", new ClickListener() {
            public void onClick(Widget sender) {
                searchPanel.addPredicate();
              }
            });
        add.setWidth("20px");
        buttons.add(add);
        dock.add(buttons, DockPanel.EAST);
        
        initWidget(dock);
    }
    
    public void addPropertySet(String setName, Map nameIdMap) {
        Object[] names = nameIdMap.keySet().toArray();
        Arrays.sort(names);
        
        propertyList.addItem("", "");
        propertyList.addItem(setName + ":", "");
        for (int i=0; i<names.length; i++) {
            String name = (String) names[i];
            String id   = (String) nameIdMap.get(name);
            
            propertyList.addItem("- " + name, id);
        }
    }

    public void addPropertySet(String setName, Collection indexes) {
        propertyList.addItem("", "");
        propertyList.addItem(setName + ":", "");
        for (Iterator itr = indexes.iterator(); itr.hasNext();) {
            WIndex wi = (WIndex)itr.next();
            String name = (String) wi.getDescription();
            String id   = (String) wi.getId();
            
            propertyList.addItem("- " + name, id);
        }
    }
    
    //
    // When the user selects a different type of search.
    // (From the first listbox)
    //
    public void processTypeChange() {
        contents.clear();
        
        matchTypeList = new ListBox();
        matchTypeList.addItem("has value",          String.valueOf(SearchPredicate.HAS_VALUE));
        matchTypeList.addItem("has value like",     String.valueOf(SearchPredicate.LIKE));
        matchTypeList.addItem("doesn't have value", String.valueOf(SearchPredicate.DOES_NOT_HAVE_VALUE));
        contents.add(matchTypeList);
        
        valueTextBox = new TextBox();
        valueTextBox.setWidth("98%");
        contents.add(valueTextBox);
        contents.setCellWidth(valueTextBox, "100%");
    }

    public SearchPredicate getPredicate()
    {
        try {
            String property = propertyList.getValue(propertyList.getSelectedIndex());
            int matchType = Integer.parseInt(matchTypeList.getValue(matchTypeList.getSelectedIndex()));
            String value = valueTextBox.getText();
            
            if (property.equals("") || value.equals(""))
                return null;
            
            return new SearchPredicate(property, matchType, value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
