package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel
    extends Composite
{
    private VerticalPanel panel;

    public SearchPanel() {
        panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.setStyleName("search-panel");
        
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
        
        //
        // Add a search predicate
        //
        this.addPredicate();
        
        initWidget(panel);
    }
    
    public void addPredicate() {
        SearchPredicate pred = new SearchPredicate(this);
        panel.add(pred);
    }
    
    public void removePredicate(SearchPredicate pred) {
        panel.remove(pred);
    }
}
