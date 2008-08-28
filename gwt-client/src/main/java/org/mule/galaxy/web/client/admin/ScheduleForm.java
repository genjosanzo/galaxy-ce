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
    private ValidatableTextBox cronTB;
    private ValidatableTextArea descriptionTA;
    private ListBox componentLB;


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
        table.setText(row++, 0, "Component:");
        table.setText(row++, 0, "Name:");
        table.setText(row++, 0, "Description:");
        table.setText(row++, 0, "Cron Command:");

        row = 0;
        componentLB = new ListBox();
        table.setWidget(row, 1, componentLB);

        row++;
        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        table.setWidget(row, 1, nameTB);
        table.setWidget(row, 2, new Label(" "));

        row++;
        descriptionTA = new ValidatableTextArea(new StringNotEmptyValidator());
        descriptionTA.getTextArea().setCharacterWidth(18);
        descriptionTA.getTextArea().setVisibleLines(4);
        table.setWidget(row, 1, descriptionTA);

        row++;
        cronTB = new ValidatableTextBox(new StringNotEmptyValidator());
        table.setWidget(row, 1, cronTB);
        table.setWidget(row, 2, new Label(" "));

        // TODO: add tooltip with cron help

        styleHeaderColumn(table);
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
        isOk &= cronTB.validate();
        return isOk;
    }

}
