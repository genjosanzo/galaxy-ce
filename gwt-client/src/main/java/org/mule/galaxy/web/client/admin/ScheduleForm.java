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

import java.util.List;

import org.mule.galaxy.web.client.ui.help.AdministrationMessages;
import org.mule.galaxy.web.client.ui.panel.InlineHelpPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.AdminServiceAsync;
import org.mule.galaxy.web.rpc.WScript;
import org.mule.galaxy.web.rpc.WScriptJob;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;


public class ScheduleForm extends AbstractAdministrationForm {

    private ListBox scriptLB;
    private TextField<String> nameTB;
    private TextField<String> cronTB;
    private TextArea descriptionTA;
    private WScriptJob job;
    private CheckBox concurrentCB;
    private static final AdministrationMessages administrationMessages = (AdministrationMessages) GWT.create(AdministrationMessages.class);

    public ScheduleForm(AdministrationPanel administrationPanel) {
        super(administrationPanel, "schedules", administrationMessages.scheduledSaved(), administrationMessages.scheduledDeleted(),
                administrationMessages.scheduledExists());

        setHelpPanel(new InlineHelpPanel(
                galaxy.getAdministrationMessages().scheduledItemTip(), 17));

    }

    protected void fetchItem(String id) {
        adminPanel.getGalaxy().getAdminService().getScriptJob(id, getFetchCallback());
    }

    protected void initializeItem(Object o) {
        job = (WScriptJob) o;
    }

    protected void initializeNewItem() {
        job = new WScriptJob();
    }

    protected void addFields(FlexTable table) {

        // a simple row counter to simplify table.setWidget() calls
        int row = 0;
        table.setText(row++, 0, administrationMessages.script());
        table.setText(row++, 0, administrationMessages.name());
        table.setText(row++, 0, administrationMessages.description());
        table.setText(row++, 0, administrationMessages.cronCommand());
        table.setText(row++, 0, administrationMessages.allowConcurrentExecution());

        row = 0;
        scriptLB = new ListBox();
        table.setWidget(row, 1, scriptLB);
        loadScripts();

        row++;
        nameTB = new TextField<String>();
        nameTB.setAllowBlank(false);
        table.setWidget(row, 1, nameTB);
        table.setWidget(row, 2, new Label(" "));
        nameTB.setValue(job.getName());

        row++;
        descriptionTA = new TextArea();
        descriptionTA.setAllowBlank(false);
        descriptionTA.setWidth(150);
        descriptionTA.setHeight(50);
        table.setWidget(row, 1, descriptionTA);
        descriptionTA.setValue(job.getDescription());

        row++;
        cronTB = new TextField<String>();
        cronTB.setAllowBlank(false);
        cronTB.setValue(job.getExpression());
        
        ToolTipConfig ttcfg = new ToolTipConfig(administrationMessages.cronHelp(), getCronHelpString());
        ttcfg.setTrackMouse(true);
        ttcfg.setAutoHide(true);
        cronTB.setToolTip(ttcfg);

        table.setWidget(row, 1, cronTB);
        
        row++;
        concurrentCB = new CheckBox();
        concurrentCB.setValue(job.isConcurrentExecutionAllowed());
        table.setWidget(row, 1, concurrentCB);
        
        styleHeaderColumn(table);
    }

    private void loadScripts() {
        adminPanel.getGalaxy().getAdminService().getScripts(new AbstractCallback<List<WScript>>(adminPanel) {

            public void onCallSuccess(List<WScript> scripts) {
                finishLoadScripts(scripts);
            }

        });
    }

    protected void finishLoadScripts(List<WScript> scripts) {
        ListBox lb = scriptLB;
        for (WScript s : scripts) {
            lb.addItem(s.getName(), s.getId());

            if (s.getId().equals(job.getScript())) {
                lb.setSelectedIndex(lb.getItemCount() - 1);
            }
        }
    }

    private String getCronHelpString() {
        FlexTable t = new FlexTable();
        t.setText(0, 0, administrationMessages.fieldName());
        t.setText(0, 1, administrationMessages.mandatory());
        t.setText(0, 2, administrationMessages.allowedValues());
        t.setText(0, 3, administrationMessages.allowedSpecialCharacters());

        t.setText(1, 0, administrationMessages.seconds());
        t.setText(1, 1, administrationMessages.yes());
        t.setText(1, 2, "0-59");
        t.setText(1, 3, ", - * / ");

        t.setText(2, 0, administrationMessages.minutes());
        t.setText(2, 1, administrationMessages.yes());
        t.setText(2, 2, "0-59");
        t.setText(2, 3, ", - * / ");

        t.setText(3, 0, administrationMessages.hours());
        t.setText(3, 1, administrationMessages.yes());
        t.setText(3, 2, "0-23");
        t.setText(3, 3, ", - * / ");

        t.setText(4, 0, administrationMessages.dayOfMonth());
        t.setText(4, 1, administrationMessages.yes());
        t.setText(4, 2, "0-31");
        t.setText(4, 3, ", - * / L W");

        t.setText(5, 0, administrationMessages.month());
        t.setText(5, 1, administrationMessages.yes());
        t.setText(5, 2, "1-12 or JAN-DEC");
        t.setText(5, 3, ", - * / ");

        t.setText(6, 0, administrationMessages.dayOfWeek());
        t.setText(6, 1, administrationMessages.yes());
        t.setText(6, 2, "1-7 or SUN-SAT");
        t.setText(6, 3, ", - * / L #");

        t.setText(7, 0, administrationMessages.year());
        t.setText(7, 1, administrationMessages.yes());
        t.setText(7, 2, "empty, 1970-2099");
        t.setText(7, 3, ", - * / ");
        return t.toString();
    }

    public String getTitle() {
        String s = (newItem) ? administrationMessages.add() : administrationMessages.edit();
        return s + administrationMessages.scheduledItem();
    }

    protected void save() {
        if (!validate()) {
            return;
        }
        super.save();

        job.setName(nameTB.getValue());
        job.setDescription(descriptionTA.getValue());
        job.setExpression(cronTB.getValue());
        job.setConcurrentExecutionAllowed(concurrentCB.getValue());
        
        ListBox lb = scriptLB;
        int selectedIndex = lb.getSelectedIndex();
        if (selectedIndex != -1) {
            job.setScript(lb.getValue(selectedIndex));
        }
        adminPanel.getGalaxy().getAdminService().save(job, getSaveCallback());
    }

    protected void delete() {
        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    ScheduleForm.super.delete();
                    AdminServiceAsync svc = adminPanel.getGalaxy().getAdminService();
                    svc.deleteScriptJob(job.getId(), getDeleteCallback());
                }
            }
        };

        MessageBox.confirm(administrationMessages.confirm(), administrationMessages.deleteSchedule() + job.getName() + "?", l);
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
