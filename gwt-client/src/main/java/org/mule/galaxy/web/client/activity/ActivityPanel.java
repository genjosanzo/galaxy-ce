package org.mule.galaxy.web.client.activity;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.GWTCDatePicker;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WActivity;
import org.mule.galaxy.web.rpc.WUser;

public class ActivityPanel extends AbstractComposite implements ErrorPanel {

    private FlowPanel panel;
    private TextBox fromTB;
    private TextBox toTB;
    private ListBox userLB;
    private FlowPanel errorPanel;
    private ListBox eventLB;
    private ListBox resultsLB;
    private final Galaxy galaxy;
    private int resultStart;
    private SimplePanel resultsPanel;
    private FlexTable table;
    
    public ActivityPanel(final Galaxy galaxy) {
        super();
        this.galaxy = galaxy;

        FlowPanel base = new FlowPanel();
        base.setStyleName("activity-base-panel");
        
        panel = new FlowPanel();
        panel.setStyleName("activity-panel");
        base.add(panel);
        
        SimplePanel searchContainer = new SimplePanel();
        searchContainer.setStyleName("activity-search-panel-container");
        panel.add(searchContainer);
        
        InlineFlowPanel searchPanel = new InlineFlowPanel();
        searchPanel.setStyleName("activity-search-panel");
        searchContainer.add(searchPanel);
        
        fromTB = createDatePicker();
        toTB = createDatePicker();
        
        searchPanel.add(new Label("From:"));        
        searchPanel.add(fromTB);

        searchPanel.add(new Label("To:"));       
        searchPanel.add(toTB);
        
        searchPanel.add(new Label("User:"));
        userLB = new ListBox();
        userLB.addItem("All");
        userLB.addItem("System");
        userLB.setSelectedIndex(0);
        searchPanel.add(userLB);
        galaxy.getUserService().getUsers(new AbstractCallback(this) {
            public void onSuccess(Object result) {
                initUsers((Collection) result);
            }
        });
        
        searchPanel.add(new Label("EventType:"));
        eventLB = new ListBox();
        eventLB.addItem("All");
        eventLB.addItem("Info");
        eventLB.addItem("Error");
        eventLB.addItem("Warning");
        eventLB.setSelectedIndex(0);
        searchPanel.add(eventLB);
        
        
        searchPanel.add(new Label("Max Results:"));
        resultsLB = new ListBox();
        resultsLB.addItem("10");
        resultsLB.addItem("25");
        resultsLB.addItem("50");
        resultsLB.addItem("100");
        resultsLB.addItem("200");
        resultsLB.setSelectedIndex(2);
        searchPanel.add(resultsLB);
        
        
        Button search = new Button("Search");
        search.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                refresh();
            }
            
        });
        searchPanel.add(search);
        
        resultsPanel = new SimplePanel();
        panel.add(resultsPanel);
        
        table = createRowTable();
        
        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");
        
        refresh();
        
        initWidget(base);
    }

    protected void initUsers(Collection result) {
        for (Iterator itr = result.iterator(); itr.hasNext();) {
            WUser user = (WUser)itr.next();
            
            userLB.addItem(user.getName() + " (" + user.getUsername() + ")", user.getId());
        }
    }
    
    public void refresh() {
        panel.remove(errorPanel);
        
        resultsPanel.clear();
        resultsPanel.add(new Label("Loading..."));
        
        String user = userLB.getItemText(userLB.getSelectedIndex());
        String eventType = eventLB.getItemText(eventLB.getSelectedIndex());
        String resultsStr = resultsLB.getItemText(resultsLB.getSelectedIndex());
        
        boolean ascending = false;
        AbstractCallback callback = new AbstractCallback(this) {

            public void onSuccess(Object o) {
                loadResults((Collection) o);
            }
            
        };
        galaxy.getRegistryService().getActivities(null, 
                                                  null, 
                                                  user, 
                                                  eventType, 
                                                  resultStart, 
                                                  new Integer(resultsStr).intValue(), 
                                                  ascending, 
                                                  callback);
    }

    protected void loadResults(Collection o) {
        resultsPanel.clear();
        resultsPanel.add(table);

        table.clear();
        
        table.setText(0, 0, "Date");
        table.setText(0, 1, "User");
        table.setText(0, 2, "Event Type");
        table.setText(0, 3, "Activity");
        
        int i = 1;
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            WActivity act = (WActivity)itr.next();

            table.setText(i, 0, act.getDate());
            if (act.getName() == null) {
                table.setText(i, 1, "System");
            } else {
                table.setText(i, 1, act.getName() + " (" + act.getUsername() + ")");
            }
            table.setText(i, 2, act.getEventType());
            table.setText(i, 3, act.getMessage());
            i++;
        }
    }

    private TextBox createDatePicker() {
        
        final TextBox tb = new TextBox();
        tb.setVisibleLength(10);
        panel.add(tb);

        final GWTCDatePicker datePicker = new GWTCDatePicker(true);
        datePicker.setMinimalDate(new Date(0));
        datePicker.setMaximalDate(new Date(Long.MAX_VALUE));
        
        datePicker.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                tb.setText(datePicker.getSelectedDateStr("yyyy-MM-dd"));
                datePicker.hide();
            }
        });

        tb.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                datePicker.show(tb);
            }

        });

        return tb;
    }
    

    public void setMessage(Label label) {
        errorPanel.clear();
        
        errorPanel.add(label);
        
        panel.insert(errorPanel, 0);
    }
    
    public void setMessage(String string) {
        setMessage(new Label(string));
    }

}
