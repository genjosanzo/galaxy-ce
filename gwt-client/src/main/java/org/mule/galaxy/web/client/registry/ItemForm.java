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

package org.mule.galaxy.web.client.registry;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.WorkspaceOracle;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableSuggestBox;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.WPolicyException;

public class ItemForm extends AbstractErrorShowingComposite {
    private ValidatableTextBox nameBox;
    private FlexTable table;
    private ValidatableTextBox versionBox;
    private final Galaxy galaxy;
    private String entryId;
    private CheckBox disablePrevious;
    private boolean add;
    private Button addButton;
    private RegistryMenuPanel menuPanel;
    private FlowPanel panel;
    private ValidatableSuggestBox workspaceSB;

    public ItemForm(final Galaxy galaxy) {
        this.galaxy = galaxy;

        menuPanel = new RegistryMenuPanel(galaxy);
        panel = new FlowPanel();
        menuPanel.setMain(panel);

        initWidget(menuPanel);
    }

    public void onHide() {
        panel.clear();

        if (nameBox != null) {
            nameBox.getTextBox().setText("");
        }
        versionBox.getTextBox().setText("");
    }

    public void onShow(List<String> params) {
        if (params.size() > 0) {
            entryId = params.get(0);
        } else {
            add = true;
            entryId = null;
        }
        
        panel.add(createPrimaryTitle("Add Entry"));

        table = createColumnTable();
        panel.add(table);

        if (add) {
            setupAddForm();
        } else {
            setupAddVersionForm(panel);
        }
        menuPanel.onShow();
    }

    private void setupRemainingTable(int row) {
        addButton = new Button("Add");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                add();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            public void onClick(final Widget widget) {
                History.back();
            }
        });

        InlineFlowPanel buttons = new InlineFlowPanel();
        buttons.add(addButton);
        buttons.add(cancel);

        table.setWidget(row + 1, 1, buttons);

        styleHeaderColumn(table);

        if (add) {
            setTitle("Add Entry");
        } else {
            setTitle("Add New Entry Version");
        }
    }

    protected void add() {
        if (!validate()) {
            return;
        }
        
        AbstractCallback callback = new AbstractCallback(menuPanel) {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof WPolicyException) {
                    PolicyPanel.handlePolicyFailure(galaxy, (WPolicyException) caught);
                } else if (caught instanceof ItemExistsException) {
                    menuPanel.setMessage("An item with this name/version already exists.");
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object id) {
                if (add) {
                    History.newItem("entry/" + (String) id);
                } else {
                    History.newItem("entry/" + entryId);
                }
            }
            
        };
        
        if (add) {
            doAdd(callback);
        } else {
            doAddVersion(callback);
        }
    }

    private void doAdd(AbstractCallback callback) {
//        galaxy.getRegistryService().newEntry(workspaceSB.getText(), nameBox.getText(), versionBox.getText(), callback);
    }
    
    private void doAddVersion(AbstractCallback callback) {
//        galaxy.getRegistryService().newEntryVersion(entryId, versionBox.getText(), callback);
    }
    
    private boolean validate() {
        menuPanel.clearErrorMessage();
        boolean v = true;
        
        if (add) {
            v &= nameBox.validate();
            v &= workspaceSB.validate();
        }
        
        v &= versionBox.validate();
        return v;
    }

    private void setupAddForm() {
        table.setWidget(0, 0, new Label("Workspace:"));

        workspaceSB = new ValidatableSuggestBox(new StringNotEmptyValidator(),
                                                new WorkspaceOracle(galaxy, menuPanel));
        table.setWidget(0, 1, workspaceSB);

        Label nameLabel = new Label("Entry Name:");
        table.setWidget(1, 0, nameLabel);

        nameBox = new ValidatableTextBox(new StringNotEmptyValidator());
        table.setWidget(1, 1, nameBox);

        Label versionLabel = new Label("Version Label:");
        table.setWidget(2, 0, versionLabel);

        versionBox = new ValidatableTextBox(new StringNotEmptyValidator());
        table.setWidget(2, 1, versionBox);

        setupRemainingTable(2);
    }

    private void setupAddVersionForm(FlowPanel panel) {
        table.setText(0, 0, "Version Label");

        versionBox = new ValidatableTextBox(new StringNotEmptyValidator());
        table.setWidget(0, 1, versionBox);;

        table.setText(1, 0, "Disable Previous");

        disablePrevious = new CheckBox();
        disablePrevious.setChecked(true);
        disablePrevious.setName("disablePrevious");
        table.setWidget(1, 1, disablePrevious);

        setupRemainingTable(1);
    }
}
