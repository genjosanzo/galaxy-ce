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

package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Tree;

public class AdminShellPanel extends AbstractAdministrationComposite {

    public AdminShellPanel(AdministrationPanel a) {
        super(a);
    }

    public void onShow() {
        super.onShow();

        FlexTable table = new FlexTable();

        FlowPanel scriptOutputPanel = new FlowPanel();
        final Label scriptResultsLabel = new Label();
        scriptResultsLabel.setWordWrap(false);
        scriptOutputPanel.add(scriptResultsLabel);
        table.setWidget(0, 0, createPrimaryTitle("Galaxy Admin Shell"));
        table.setWidget(1, 0, new Label("Type or paste a Groovy script to be executed on the server. A return value will be displayed below the area. " +
                "Tips: \n   Spring's context is available as 'applicationContext' variable" +
                "\n   only String return values are supported (or null)"));
        table.getFlexCellFormatter().setColSpan(1, 0, 2);
        final TextArea scriptArea = new TextArea();
        scriptArea.setCharacterWidth(100);
        scriptArea.setVisibleLines(30);
        table.setWidget(2, 0, scriptArea);

        // insert tree of availalb scipts here
        Tree scripts = new Tree();
        scripts.addItem("some script 1");
        scripts.addItem("some script 2");
        table.setWidget(2, 1, scripts);


        final Button evaluateBtn = new Button("Evaluate");
        evaluateBtn.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                evaluateBtn.setEnabled(false);
                adminPanel.getGalaxy().getAdminService().executeScript(scriptArea.getText(), new AbstractCallback(adminPanel) {
                    public void onFailure(Throwable caught) {
                        evaluateBtn.setEnabled(true);
                        scriptResultsLabel.setText("");
                        super.onFailure(caught);
                    }

                    public void onSuccess(Object o) {
                        adminPanel.clearErrorMessage();
                        evaluateBtn.setEnabled(true);
                        scriptResultsLabel.setText(o == null ? "No value returned" : o.toString());
                    }
                });
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                History.back();
            }
        });

        InlineFlowPanel buttons = new InlineFlowPanel();
        buttons.add(evaluateBtn);
        buttons.add(cancelBtn);

        // allow users to save the script
        CheckBox saveAsCB = new CheckBox("Save Script As ... ");
        final ValidatableTextBox saveAsTB = new ValidatableTextBox(new StringNotEmptyValidator());
        final Button saveBtn = new Button("Save");
        saveAsTB.getTextBox().setEnabled(false);
        saveBtn.setEnabled(false);
        // if users clicks checkbox, enable the text field and save button
        saveAsCB.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                saveAsTB.getTextBox().setEnabled(true);
                saveBtn.setEnabled(true);
            }
        });

        saveBtn.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                saveBtn.setEnabled(false);
                /*
                public void onFailure(Throwable caught) {
                        super.onFailure(caught);
                    }

                    public void onSuccess(Object o) {
                        adminPanel.clearErrorMessage();
                        saveBtn.setEnabled(true);
                    }
                });
                */
            }
        });

        buttons.add(saveAsCB);
        buttons.add(saveAsTB);
        buttons.add(saveBtn);

        table.setWidget(3, 0, buttons);
        table.setWidget(4, 0, scriptOutputPanel);

        panel.add(table);
        // spacer above the footer
        panel.add(new Label(" ")); // spacer
    }
}