package org.mule.galaxy.web.client.activity;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
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
    private FlowPanel resultsPanel;
    private FlexTable table;
    private int maxResults;

    public ActivityPanel(final Galaxy galaxy) {
        super();
        this.galaxy = galaxy;

        SimplePanel mainPanel = new SimplePanel();
        mainPanel.setStyleName("main-panel");

        FlowPanel base = new FlowPanel();
        base.setStyleName("activity-base-panel");
        mainPanel.add(base);

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
        userLB.addItem("System", "system");
        userLB.setSelectedIndex(0);
        searchPanel.add(userLB);
        galaxy.getUserService().getUsers(new AbstractCallback(this) {
            public void onSuccess(Object result) {
                initUsers((Collection)result);
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
                onShow();
            }

        });
        searchPanel.add(search);

        resultsPanel = new FlowPanel();
        panel.add(resultsPanel);

        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");

        initWidget(mainPanel);
    }

    protected void initUsers(Collection result) {
        for (Iterator itr = result.iterator(); itr.hasNext();) {
            WUser user = (WUser)itr.next();

            userLB.addItem(user.getName() + " (" + user.getUsername() + ")", user.getId());
        }
    }

    public void onShow() {
        panel.remove(errorPanel);

        resultsPanel.clear();
        resultsPanel.add(new Label("Loading..."));

        String user = userLB.getValue(userLB.getSelectedIndex());
        String eventType = eventLB.getItemText(eventLB.getSelectedIndex());
        maxResults = new Integer(resultsLB.getItemText(resultsLB.getSelectedIndex())).intValue();

        boolean ascending = false;
        AbstractCallback callback = new AbstractCallback(this) {

            public void onSuccess(Object o) {
                loadResults((Collection)o);
            }

        };
        String fromStr = fromTB.getText();
        String toStr = toTB.getText();
        Date fromDate = null;
        if (fromStr != null && !"".equals(fromStr)) {
            try {
                fromDate = parseDate(fromStr);
            } catch (DateParseException e) {
                setMessage("\"From\" date is invalid. It must be in the form of YYYY-MM-DD.");
                return;
            }
        } 
        
        Date toDate = null;
        if (toStr != null && !"".equals(toStr)) {
            try {
                toDate = parseDate(toStr);
            } catch (DateParseException e) {
                setMessage("\"From\" date is invalid. It must be in the form of YYYY-MM-DD.");
                return;
            }
        }
        galaxy.getRegistryService().getActivities(fromDate, toDate, user, eventType, resultStart, maxResults,
                                                  ascending, callback);
    }

    private Date parseDate(String s) throws DateParseException {
        if (s.length() != 10 || s.charAt(4) != '-' || s.charAt(7) != '-') {
            throw new DateParseException();
        }
        
        String yearStr = s.substring(0, 4);
        String monthStr = s.substring(5, 7);
        String dayStr = s.substring(8, 10);
        
        try {
            int year = new Integer(yearStr).intValue();
            int month = new Integer(monthStr).intValue();
            int day = new Integer(dayStr).intValue();
            return new Date(year-1900, month-1, day);
        } catch (NumberFormatException e) {
            throw new DateParseException();
        }
    }

    protected void loadResults(Collection o) {
        resultsPanel.clear();

        if (o.size() == maxResults || resultStart > 0) {
            FlowPanel activityNavPanel = new FlowPanel();
            activityNavPanel.setStyleName("activity-nav-panel");
            Hyperlink hl = null;
            
            if (o.size() == maxResults) {
                hl = new Hyperlink("Next", "next");
                hl.setStyleName("activity-nav-next");
                hl.addClickListener(new ClickListener() {
    
                    public void onClick(Widget arg0) {
                        resultStart += maxResults;
                        
                        onShow();
                    }
                    
                });
                activityNavPanel.add(hl);
            }
            
            if (resultStart > 0) {
                hl = new Hyperlink("Previous", "previous");
                hl.setStyleName("activity-nav-previous");
                hl.addClickListener(new ClickListener() {
    
                    public void onClick(Widget arg0) {
                        resultStart = resultStart - maxResults;
                        if (resultStart < 0) resultStart = 0;
                        
                        onShow();
                    }
                    
                });
                activityNavPanel.add(hl);
            }
            SimplePanel spacer = new SimplePanel();
            spacer.add(new HTML("&nbsp;"));
            activityNavPanel.add(spacer);
            
            resultsPanel.insert(activityNavPanel, 0);
        }
        
        table = createRowTable();
        resultsPanel.add(table);

        table.setText(0, 0, "Date");
        table.setText(0, 1, "User");
        table.setText(0, 2, "Event Type");
        table.setText(0, 3, "Activity");

        int i = 1;
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            WActivity act = (WActivity)itr.next();

            table.setText(i, 0, act.getDate());
            table.getCellFormatter().setStyleName(i, 0, "activityTableDate");

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
        
        datePicker.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                datePicker.hide();
                tb.setText(datePicker.getSelectedDateStr("yyyy-MM-dd"));
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
