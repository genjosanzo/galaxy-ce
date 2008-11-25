/**
 * 
 */
package org.mule.galaxy.web.client.registry;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;

public class ViewSearchForm extends SearchForm {

    private final ViewPanel viewPanel;
    private Button test;
    private Button delete;
    private Button cancel;
    private ValidatableTextBox nameTB;
    private CheckBox sharedCB;
    
    public ViewSearchForm(ViewPanel viewPanel, Galaxy galaxy, String searchText) {
        super(galaxy, searchText, false);
        this.viewPanel = viewPanel;
        
        initialize();
    }

    protected void initializeButtons(FlowPanel buttonPanel, String searchText) {
        test = new Button();
        test.setText("Test");
        test.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {   
                viewPanel.setTestSearch(true);
                viewPanel.refresh();
            }
        });
        buttonPanel.add(test);
        
        super.initializeButtons(buttonPanel, searchText);

        buttonPanel.remove(buttonPanel.getWidgetCount()-1);
        delete = new Button();
        delete.setText("Delete");
        delete.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                viewPanel.delete();
            }
        });

        buttonPanel.add(delete);

        cancel = new Button();
        cancel.setText("Cancel");
        cancel.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                viewPanel.setStale(true);
                if (ViewPanel.NEW_VIEW_ID.equals(viewPanel.getViewId())) {
                    History.back();
                } else {
                    // Browse back to the view
                    History.newItem("view/" + viewPanel.getViewId());
                }

            }
        });
        buttonPanel.add(cancel);
    }

    protected void initializeTopInfo(FlexTable table) {
        if (ViewPanel.NEW_VIEW_ID.equals(viewPanel.getViewId())) {
            table.setWidget(0, 0, createTitleText("New View"));
        } else {
            table.setWidget(0, 0, createTitleText("Edit View"));
        }
        table.getFlexCellFormatter().setColSpan(0, 0, 2);

        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setVisibleLength(25);
        table.setText(1, 0, "View Name: ");
        table.setWidget(1, 1, nameTB);

        sharedCB = new CheckBox("Shared");
        table.getFlexCellFormatter().setColSpan(2, 0, 2);
        table.setWidget(2, 0, sharedCB);
    }

    public Button getDelete() {
        return delete;
    }

    public Button getCancel() {
        return cancel;
    }

    public ValidatableTextBox getNameTB() {
        return nameTB;
    }

    public CheckBox getSharedCB() {
        return sharedCB;
    }
}