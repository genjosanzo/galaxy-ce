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

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WScript;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;
import java.util.List;

public class AdminShellPanel extends AbstractAdministrationComposite
        implements ClickListener, KeyboardListener {


    private Tree scriptTree;
    private FlexTable table;
    private Button cancelBtn;
    private Button evaluateBtn;
    private CheckBox saveAsCB;
    private ValidatableTextBox saveAsTB;
    private Button saveBtn;
    private TextArea scriptArea;
    private Label scriptResultsLabel;

    public AdminShellPanel(AdministrationPanel a) {
        super(a);
        initLocalWidgets();
    }

    /* create objects, set initial state including listeners */
    private void initLocalWidgets() {

        // display existing scripts in a tree
        scriptTree = new Tree();
        // main page layout
        table = new FlexTable();

        saveBtn = new Button("Save");
        saveBtn.setEnabled(false);
        saveBtn.addClickListener(this);

        cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener(this);

        evaluateBtn = new Button("Evaluate");
        evaluateBtn.addClickListener(this);

        saveAsCB = new CheckBox("Save Script As ... ");
        saveAsCB.addClickListener(this);

        saveAsTB = new ValidatableTextBox(new StringNotEmptyValidator());
        saveAsTB.getTextBox().setEnabled(false);

        // where the scripts are pasted into
        scriptArea = new TextArea();
        scriptArea.setCharacterWidth(80);
        scriptArea.setVisibleLines(30);

        scriptResultsLabel = new Label();


    }


    public void onShow() {
        super.onShow();

        table.setStyleName("admin-shell-table");
        // text area to paste script into
        table.setWidget(0, 0, createPrimaryTitle("Galaxy Admin Shell"));
        table.setWidget(1, 0, new Label("Type or paste a Groovy script to be executed on the server. A return value will be displayed below the area. " +
                "Tips: \n   Spring's context is available as 'applicationContext' variable" +
                "\n   only String return values are supported (or null)"));
        table.getFlexCellFormatter().setColSpan(1, 0, 2);
        table.setWidget(2, 0, scriptArea);

        // create a tree of available scripts
        adminPanel.getGalaxy().getAdminService().getScripts(new AbstractCallback(adminPanel) {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object o) {
                adminPanel.clearErrorMessage();
                addTreeItems((List<WScript>) o);
            }
        });


        table.setWidget(2, 1, scriptTree);
        table.getCellFormatter().setVerticalAlignment(2, 1, HasAlignment.ALIGN_TOP);
        table.getCellFormatter().setHorizontalAlignment(2, 1, HasAlignment.ALIGN_LEFT);

        // script results
        FlowPanel scriptOutputPanel = new FlowPanel();
        scriptResultsLabel.setWordWrap(false);
        scriptOutputPanel.add(scriptResultsLabel);

        // add user control buttons
        InlineFlowPanel buttons = new InlineFlowPanel();
        buttons.add(evaluateBtn);
        buttons.add(cancelBtn);
        buttons.add(saveAsCB);
        buttons.add(saveAsTB);
        buttons.add(saveBtn);

        table.setWidget(3, 0, buttons);

        // results of script execution
        table.setWidget(4, 0, scriptOutputPanel);
        panel.add(table);
    }


    // populate the availalb e
    private void addTreeItems(List<WScript> scripts) {
        for (Iterator<WScript> itr = scripts.iterator(); itr.hasNext();) {
            WScript s = itr.next();
            scriptTree.addItem(s.getName());
        }

    }


    // only use one listener for all events. It's less overheard and easier to read
    public void onClick(Widget sender) {
        if (sender == saveBtn) {
            saveBtn.setEnabled(false);

            WScript ws = new WScript();
            ws.setScript(scriptArea.getText());
            adminPanel.getGalaxy().getAdminService().save(ws, new AbstractCallback(adminPanel) {
                public void onFailure(Throwable caught) {
                    super.onFailure(caught);
                }

                public void onSuccess(Object o) {
                    adminPanel.clearErrorMessage();
                    adminPanel.setMessage("Script Saved.");
                    saveBtn.setEnabled(true);
                }
            });
        }

        if (sender == saveAsCB) {
            saveAsTB.getTextBox().setEnabled(true);
            saveAsTB.getTextBox().setFocus(true);
            saveBtn.setEnabled(true);
        }

        if (sender == cancelBtn) {
            scriptArea.setText("");
            saveAsCB.setEnabled(false);
            History.back();
        }

        if (sender == evaluateBtn) {
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

    }


    public void onKeyPress(Widget widget, char keyCode, int modifiers) {
        if ((keyCode == KEY_ENTER) && (modifiers == 0)) {
            // FIXME: save
        }
    }


    public void onKeyDown(Widget widget, char c, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onKeyUp(Widget widget, char c, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
