/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-08-25 00:59:35Z mark $
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

package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableListBox;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextArea;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Things we can do:
 * 1. Add a new scheduled item with a new component
 * 2. Create a new entry with an existing component
 * 3. Edit a scheduled item's properties
 */
public class ScheduleForm extends AbstractAdministrationForm {

    private ValidatableTextBox nameTB;
    private ValidatableTextBox commandTB;
    private ValidatableTextArea descriptionTA;

    // Quartz params
    private ValidatableListBox secondLB;
    private ValidatableListBox minuteLB;
    private ValidatableListBox hourLB;
    private ValidatableListBox dayOfMonthLB;
    private ValidatableListBox monthLB;
    private ValidatableListBox dayOfWeekLB;
    private ListBox yearLB;

    // the component to schedule will either be selected from existing ones
    // or uploaded via a form -- the user must choose.
    private CheckBox componentCB;
    private ListBox componentLB;
    private FileUpload componentUpload;


    public ScheduleForm(AdministrationPanel administrationPanel) {
        super(administrationPanel, "schedules", "Scheduled item was saved.", "Scheduled item was deleted.",
              "A Scheduled item with that name already exists");
    }

    protected void fetchItem(String id) {
    }

    protected void initializeItem(Object o) {
    }

    protected void initializeNewItem() {
    }

    protected void addFields(FlexTable table) {
        // a simple row counter to simplify table.setWidget() calls
        int row = 0;
        table.setText(row++, 0, "Name:");
        table.setText(row++, 0, "Description:");
        table.setText(row++, 0, "Command:");
        table.setText(row++, 0, "Second:");
        table.setText(row++, 0, "Minute:");
        table.setText(row++, 0, "Hour:");
        table.setText(row++, 0, "Day of Month:");
        table.setText(row++, 0, "Month:");
        table.setText(row++, 0, "Day of Week:");
        table.setText(row++, 0, "Year:");

        row = 0;
        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        this.addValidationFormWidget(table, nameTB, row);

        row++;
        descriptionTA = new ValidatableTextArea(new StringNotEmptyValidator());
        descriptionTA.getTextArea().setCharacterWidth(40);
        descriptionTA.getTextArea().setVisibleLines(4);
        this.addValidationFormWidget(table, descriptionTA, row);

        row++;
        commandTB = new ValidatableTextBox(new StringNotEmptyValidator());
        this.addValidationFormWidget(table, commandTB, row);

        // init quartz "cron" params
        row++;
        secondLB = new ValidatableListBox(new StringNotEmptyValidator(), true);
        secondLB.getListBox().setVisibleItemCount(5);
        this.addValidationFormWidget(table, secondLB, row);

        row++;
        minuteLB = new ValidatableListBox(new StringNotEmptyValidator(), true);
        minuteLB.getListBox().setVisibleItemCount(5);
        this.addValidationFormWidget(table, minuteLB, row);

        row++;
        hourLB = new ValidatableListBox(new StringNotEmptyValidator(), true);
        hourLB.getListBox().setVisibleItemCount(5);
        this.addValidationFormWidget(table, hourLB, row);

        row++;
        dayOfMonthLB = new ValidatableListBox(new StringNotEmptyValidator(), true);
        dayOfMonthLB.getListBox().setVisibleItemCount(5);
        this.addValidationFormWidget(table, dayOfMonthLB, row);

        row++;
        monthLB = new ValidatableListBox(new StringNotEmptyValidator(), true);
        monthLB.getListBox().setVisibleItemCount(5);
        this.addValidationFormWidget(table, monthLB, row);

        row++;
        dayOfWeekLB = new ValidatableListBox(new StringNotEmptyValidator(), true);
        dayOfWeekLB.getListBox().setVisibleItemCount(5);
        this.addValidationFormWidget(table, dayOfWeekLB, row);

        row++;
        yearLB = new ListBox(true);
        yearLB.setVisibleItemCount(5);
        this.addValidationFormWidget(table, yearLB, row);

        styleHeaderColumn(table);
    }

    private void addValidationFormWidget(FlexTable table, Widget w, int row) {
        w.setWidth("100%");
        table.setWidget(row, 1, w);
        table.setWidget(row, 2, new Label(" "));
        table.getCellFormatter().setWidth(row, 2, "100%");
    }



    public String getTitle() {
        String s = (newItem) ? "Add" : "Edit";
        return s + " Scheduled Item";
    }

    protected void save() {
        if (!validate()) {
            return;
        }
        super.save();
    }


    protected boolean validate() {
        getErrorPanel().clearErrorMessage();
        boolean isOk = true;

        if (newItem) {
            isOk &= nameTB.validate();
        }
        isOk &= nameTB.validate();
        isOk &= commandTB.validate();
        return isOk;
    }

}
