package org.mule.galaxy.web.client;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanelRow
    extends Composite
{
    private SearchPanel searchPanel;
    private ListBox     attributeList;
    private HorizontalPanel contents;
    
    public SearchPanelRow(SearchPanel sp) {
        super();
        
        searchPanel = sp;
        
        DockPanel dock = new DockPanel();
        dock.setStylePrimaryName("search-predicate");
        dock.setWidth("100%");
        
        attributeList = new ListBox();
        attributeList.setWidth("175px");
        attributeList.addItem("Name", "name");
        attributeList.addChangeListener(new ChangeListener() {
           public void onChange(Widget sender) {
               processTypeChange();
           }
        });
        dock.add(attributeList, DockPanel.WEST);
        
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
    
    public void setAttributeList(Map nameIdMap) {
        Set names = nameIdMap.keySet();
        for (Iterator itr = names.iterator(); itr.hasNext();) {
            String name = (String) itr.next();
            String id   = (String) nameIdMap.get(name);
            
            attributeList.addItem(name, id);
        }
    }
    
    //
    // When the user selects a different type of search.
    // (From the first listbox)
    //
    public void processTypeChange() {
        contents.clear();
        
        ListBox lb = new ListBox();
        lb.addItem("matches");
        lb.addItem("contains");
        lb.addItem("begins with");
        lb.addItem("ends with");
        lb.addItem("is");
        contents.add(lb);
        
        TextBox tb = new TextBox();
        tb.setWidth("98%");
        contents.add(tb);
        contents.setCellWidth(tb, "100%");
    }
}
