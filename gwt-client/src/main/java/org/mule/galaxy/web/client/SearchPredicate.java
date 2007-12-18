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

public class SearchPredicate
    extends Composite
{
    private SearchPanel searchPanel;
    private ListBox     listBox;
    private HorizontalPanel contents;
    
    public SearchPredicate(SearchPanel sp) {
        super();
        
        searchPanel = sp;
        
        DockPanel dock = new DockPanel();
        dock.setWidth("100%");
        
        listBox = new ListBox();
        listBox.setWidth("100px");
        listBox.addItem("Name");
        listBox.addItem("Other");
        listBox.addChangeListener(new ChangeListener() {
           public void onChange(Widget sender) {
               processTypeChange();
           }
        });
        dock.add(listBox, DockPanel.WEST);
        
        contents = new HorizontalPanel();
        contents.setWidth("100%");
        dock.add(contents, DockPanel.CENTER);
        dock.setCellWidth(contents, "100%");
        processTypeChange();
        
        HorizontalPanel buttons = new HorizontalPanel();
        final SearchPredicate pred = this;
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
        tb.setText(listBox.getItemText(listBox.getSelectedIndex()));
        contents.add(tb);
        contents.setCellWidth(tb, "100%");
    }
}
