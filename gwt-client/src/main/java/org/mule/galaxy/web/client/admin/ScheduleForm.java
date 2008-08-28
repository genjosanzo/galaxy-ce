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
import org.mule.galaxy.web.client.validation.ui.ValidatableTextArea;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.client.validation.ui.ValidatableListBox;
import org.mule.galaxy.web.client.util.TooltipListener;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Image;


public class ScheduleForm extends AbstractAdministrationForm {

    private ValidatableListBox scriptLB;
    private ValidatableTextBox nameTB;
    private ValidatableTextBox cronTB;
    private ValidatableTextArea descriptionTA;


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
        scriptLB = new ValidatableListBox(new StringNotEmptyValidator());
        table.setWidget(row, 1, scriptLB);

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
        Image help = new Image("images/help_16x16.gif");
        help.addMouseListener(new TooltipListener(getCronHelpString(),
                                                      10000));
        table.setWidget(row, 2, help);

        // TODO: add tooltip with cron help
        styleHeaderColumn(table);
    }

    // hmmmmmm....
    private String getCronHelpString() {
        String s = "" +
        "<table>" +
        "<tr>" +
                "<td><b>Field Name</b></td>" +
                "<td><b>Mandatory?</b></td>" +
                "<td><b>Allowed Values</b></td>" +
                "<td><b>Allowed Special Characters</b></td>" +
        "</tr>" +
        "<tr>" +
                "<td>Seconds</td>" +
                "<td>YES</td>" +
                "<td>0-59</td>" +
                "<td>, - * / </td>" +
        "</tr>" +
        "<tr>" +
                "<td>Minutes</td>" +
                "<td>YES</td>" +
                "<td>0-59</td>" +
                "<td>, - * / </td>" +
        "</tr>" +
        "<tr>" +
                "<td>Hours</td>" +
                "<td>YES</td>" +
                "<td>0-23</td>" +
                "<td>, - * / </td>" +
        "</tr>" +
        "<tr>" +
                "<td>Day Of Month</td>" +
                "<td>YES</td>" +
                "<td>1-31</td>" +
                "<td>, - * / L W</td>" +
        "</tr>" +
        "<tr>" +
                "<td>Month</td>" +
                "<td>YES</td>" +
                "<td>1-12 or JAN-DEC</td>" +
                "<td>, - * / </td>" +
        "</tr>" +
        "<tr>" +
                "<td>Day Of Week</td>" +
                "<td>YES</td>" +
                "<td>1-7 or SUN-SAT</td>" +
                "<td>, - * / L #</td>" +
        "</tr>" +
        "<tr>" +
                "<td>Year</td>" +
                "<td>NO</td>" +
                "<td>empty, 1970-2099</td>" +
                "<td>, - * / </td>" +
        "</tr>" +
        "</table>";

        return s;
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

        //isOk &= scriptLB.validate();
        isOk &= nameTB.validate();
        isOk &= cronTB.validate();
        return isOk;
    }

}
