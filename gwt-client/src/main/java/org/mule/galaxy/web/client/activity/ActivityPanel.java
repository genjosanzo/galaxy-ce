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

package org.mule.galaxy.web.client.activity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widgetideas.client.event.KeyDownEvent;
import com.google.gwt.widgetideas.client.event.KeyDownHandler;
import com.google.gwt.widgetideas.datepicker.client.DateBox;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.ItemPathOracle;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WActivity;
import org.mule.galaxy.web.rpc.WUser;

public class ActivityPanel extends AbstractFlowComposite {

    private ListBox userLB;
    private ListBox eventLB;
    private ListBox resultsLB;
    private final Galaxy galaxy;
    private int resultStart;
    private FlowPanel resultsPanel;
    private FlexTable table;
    private int maxResults;
    private SuggestBox itemSB;
    private TextBox textTB;
    private DateBox startDate;
    private DateBox endDate;
    private final ErrorPanel errorPanel;

    public ActivityPanel(ErrorPanel errorPanel, final Galaxy galaxy) {
        super();
        this.errorPanel = errorPanel;
        this.galaxy = galaxy;
    }

    public void initialize() {

        FlowPanel searchContainer = new FlowPanel();
        searchContainer.setStyleName("activity-search-panel-container");
        panel.add(searchContainer);

        InlineFlowPanel searchPanel = new InlineFlowPanel();
        searchPanel.setStyleName("activity-search-panel");
        searchContainer.add(searchPanel);

        FlexTable searchTable = new FlexTable();
        searchTable.setCellSpacing(3);
        searchPanel.add(searchTable);

        startDate = new DateBox();
        endDate = new DateBox();

        DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
        startDate.setAnimationEnabled(true);
        startDate.setDateFormat(dateFormat);
        endDate.setAnimationEnabled(true);
        endDate.setDateFormat(dateFormat);

        startDate.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent e) {
                if (e.getKeyCode() == KEY_RIGHT
                        && startDate.getCursorPos() == startDate.getText().length()) {
                    startDate.hideDatePicker();
                    endDate.setFocus(true);
                }
            }
        });

        endDate.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent e) {
                if ((e.getKeyCode() == KEY_LEFT) && endDate.getCursorPos() == 0) {
                    startDate.setFocus(true);
                    endDate.hideDatePicker();
                }
            }
        });

        // always start with today's date
        startDate.showDate(new Date());

        searchTable.setWidget(0, 0, new Label("From:"));
        searchTable.setWidget(0, 1, startDate);

        searchTable.setWidget(1, 0, new Label("To:"));
        searchTable.setWidget(1, 1, endDate);

        userLB = new ListBox();
        userLB.addItem("All");
        userLB.addItem("System", "system");
        searchPanel.add(userLB);
        galaxy.getSecurityService().getUsers(new AbstractCallback(errorPanel) {
            public void onSuccess(Object result) {
                initUsers((Collection) result);
            }
        });

        searchTable.setWidget(0, 2, new Label("User:"));
        searchTable.setWidget(0, 3, userLB);

        searchTable.setWidget(1, 2, new Label("Type:"));
        eventLB = new ListBox();
        eventLB.addItem("All");
        eventLB.addItem("Info");
        eventLB.addItem("Error");
        eventLB.addItem("Warning");
        searchTable.setWidget(1, 3, eventLB);

        searchTable.setWidget(0, 4, new Label("Text Contains:"));
        textTB = new TextBox();
        searchTable.setWidget(0, 5, textTB);

        searchTable.setWidget(1, 4, new Label("Relating to:"));
        itemSB = new SuggestBox(new ItemPathOracle(galaxy, errorPanel));
        itemSB.setText("[All Items]");
        searchTable.setWidget(1, 5, itemSB);


        searchTable.setWidget(0, 6, new Label("Max Results:"));
        resultsLB = new ListBox();
        resultsLB.addItem("10");
        resultsLB.addItem("25");
        resultsLB.addItem("50");
        resultsLB.addItem("100");
        resultsLB.addItem("200");
        searchTable.setWidget(0, 7, resultsLB);

        Button search = new Button("Search");
        search.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                onShow();
            }

        });

        Button reset = new Button("Reset");
        reset.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                reset();
                onShow();
            }

        });
        InlineFlowPanel btnPanel = new InlineFlowPanel();
        btnPanel.setStyleName("activity-search-panel");
        btnPanel.add(search);
        btnPanel.add(reset);
        searchContainer.add(btnPanel);

        resultsPanel = new FlowPanel();
        panel.add(resultsPanel);

        // set form widgets to default values
        reset();
    }

    protected void initUsers(Collection result) {
        for (Iterator itr = result.iterator(); itr.hasNext();) {
            WUser user = (WUser) itr.next();

            userLB.addItem(user.getName() + " (" + user.getUsername() + ")", user.getId());
        }
    }
    

    public void onShow() {
        if (panel.getWidgetCount() == 0) {
            initialize();
        }
        errorPanel.clearErrorMessage();

        resultsPanel.clear();
        resultsPanel.add(new Label("Loading..."));

        String user = userLB.getValue(userLB.getSelectedIndex());
        String eventType = eventLB.getItemText(eventLB.getSelectedIndex());
        maxResults = new Integer(resultsLB.getItemText(resultsLB.getSelectedIndex())).intValue();

        boolean ascending = false;
        AbstractCallback callback = new AbstractCallback(errorPanel) {

            public void onSuccess(Object o) {
                loadResults((Collection) o);
            }

        };
        String fromStr = startDate.getText();
        String toStr = endDate.getText();
        Date fromDate = null;
        if (fromStr != null && !"".equals(fromStr)) {
            try {
                fromDate = parseDate(fromStr);
            } catch (DateParseException e) {
                errorPanel.setMessage("\"From\" date is invalid. It must be in the form of YYYY-MM-DD.");
                return;
            }
        }

        Date toDate = null;
        if (toStr != null && !"".equals(toStr)) {
            try {
                toDate = parseDate(toStr);
            } catch (DateParseException e) {
                errorPanel.setMessage("\"From\" date is invalid. It must be in the form of YYYY-MM-DD.");
                return;
            }
        }
        galaxy.getRegistryService().getActivities(fromDate, toDate, user,
                itemSB.getText(),
                textTB.getText(),
                eventType, resultStart, maxResults,
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
            return new Date(year - 1900, month - 1, day);
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
                hl.addClickHandler(new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        resultStart += maxResults;

                        onShow();
                    }

                });
                activityNavPanel.add(hl);
            }

            if (resultStart > 0) {
                hl = new Hyperlink("Previous", "previous");
                hl.setStyleName("activity-nav-previous");
                hl.addClickHandler(new ClickHandler() {

                    public void onClick(ClickEvent event) {
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
        table.setText(0, 2, "Type");
        table.setText(0, 3, "Activity");

        int i = 1;
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            WActivity act = (WActivity) itr.next();

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


    // reset search params to default values
    private void reset() {
        userLB.setSelectedIndex(0);
        eventLB.setSelectedIndex(0);
        resultsLB.setSelectedIndex(2);
    }

}
