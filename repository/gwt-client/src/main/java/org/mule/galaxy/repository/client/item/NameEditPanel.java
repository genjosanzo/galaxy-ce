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

package org.mule.galaxy.repository.client.item;

import org.mule.galaxy.repository.client.admin.PolicyPanel;
import org.mule.galaxy.repository.client.util.RegistryOracle;
import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WPolicyException;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.History;

/**
 * A panel for editing the name of a registry entry.
 */
public class NameEditPanel extends LayoutContainer {

    private final RepositoryMenuPanel menuPanel;
    private RegistryServiceAsync registryService;
    private final Window window;
    private final ItemInfo item;
    private Button saveButton;
    private Button cancelButton;
    private ComboBox fwspace;
    private final boolean allowRelocating;

    public NameEditPanel(RepositoryMenuPanel menuPanel,
                         ItemInfo item,
                         final Window window) {
        this(menuPanel, item, true, window);
    }
    

    public NameEditPanel(RepositoryMenuPanel menuPanel,
                         ItemInfo item,
                         boolean allowRelocating,
                         final Window window) {
        super();
        this.menuPanel = menuPanel;
        this.item = item;
        this.allowRelocating = allowRelocating;
        this.window = window;
        this.registryService = menuPanel.getRepositoryModule().getRegistryService();

        TableLayout layout = new TableLayout(2);
        layout.setCellSpacing(10);
        setLayout(layout);
        
        if (allowRelocating) {
            add(new Label("Parent:"));
            String parentPath = item.getParentPath();
            if ("".equals(parentPath) || null == parentPath) {
                parentPath = "/";
            }
            
            String parentType = "Workspace";
            
            RegistryOracle wkspcOracle = new RegistryOracle(registryService, parentType);
            fwspace = wkspcOracle.getComboBox();
            fwspace.setFieldLabel("Parent");
            fwspace.setAllowBlank(false);
            fwspace.setName("workspacePath");
            fwspace.setValueField("fullPath");
            fwspace.setWidth(217);
            
            ModelData defaultModel = new BaseModelData();
            defaultModel.set("fullPath", parentPath);
            fwspace.setValue(defaultModel);
            
            add(fwspace);
        }
        
        add(new Label("Name:"));
        final TextField<String> nameTB = new TextField<String>();
        nameTB.setValue(item.getName());
        nameTB.setWidth(200);

        add(nameTB);
        
        HorizontalPanel buttons = new HorizontalPanel();
        saveButton = new Button("Save", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!validateName(nameTB)) {
                    return;
                }
                save(getParentPath(), nameTB.getValue());
            }
            
        });

        cancelButton = new Button("Cancel", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                window.hide();
            }
            
        });

        buttons.add(saveButton);
        buttons.add(new Label(" "));
        buttons.add(cancelButton);
        TableData buttonData = new TableData();
        buttonData.setColspan(2);
        add(buttons, buttonData);   
    }

    public String getParentPath() {
        if (allowRelocating) {
            return (String)fwspace.getValue().get("fullPath");
        } else {
            return item.getParentPath(); 
        }
    }

    protected void save(final String newParent, final String newName) {
        if (!newParent.equals(item.getParentPath()) 
            || !newName.equals(item.getName())) {
            // save only if name or workspace changed
            setEnabled(false);
            registryService.move(item.getId(), newParent, newName, new AbstractCallback(menuPanel) {

                @Override
                public void onCallFailure(Throwable caught) {
                    setEnabled(true);
                    if (caught instanceof ItemNotFoundException) {
                        menuPanel.setMessage("No parent workspace exists with that name!");
                    } else if (caught instanceof WPolicyException) {
                        PolicyPanel.handlePolicyFailure(menuPanel.getGalaxy(), (WPolicyException) caught);
                    } else {
                        super.onFailure(caught);
                    }
                }

                public void onCallSuccess(Object arg0) {
                    // need to refresh the whole panel to fetch new workspace location and entry name
                    item.setName(newName);
                    window.hide();     
                    History.fireCurrentHistoryState();
                    menuPanel.refresh();
                }

            });
        } else {
            // restore the original view
            window.hide();
        }
    }

    public void setEnabled(boolean b) {
        saveButton.setEnabled(b);
        cancelButton.setEnabled(b);
    }
        
    protected boolean validateName(final TextField nameTB) {
        boolean isOk = true;
        isOk &= nameTB.validate();

        return isOk;
    }
}
