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

import java.util.List;

import org.mule.galaxy.repository.client.admin.PolicyPanel;
import org.mule.galaxy.repository.client.util.ItemPathOracle;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WPolicyException;
import org.mule.galaxy.web.client.ui.field.ValidatableTextBox;
import org.mule.galaxy.web.client.ui.panel.InlineFlowPanel;
import org.mule.galaxy.web.client.ui.validator.StringNotEmptyValidator;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel for editing the name of a registry entry.
 */
public class NameEditPanel extends Composite {

    private InlineFlowPanel panel;
    private final String itemId;
    private String name;
    private final String workspacePath;
    private final RepositoryMenuPanel menuPanel;
    private final List<String> callbackParams;
    private RegistryServiceAsync registryService;

    public NameEditPanel(RepositoryMenuPanel menuPanel,
                         String itemId,
                         String name,
                         String workspacePath,  
                         final List<String> callbackParams) {
        super();
        this.menuPanel = menuPanel;
        this.itemId = itemId;
        this.name = name;
        this.workspacePath = workspacePath;
        this.registryService = menuPanel.getRepositoryModule().getRegistryService();

        panel = new InlineFlowPanel();
        this.callbackParams = callbackParams;

        initName();
        
        initWidget(panel);
    }

    private void initName() {
        panel.add(new Label(name + " "));

        Image editImg = new Image("images/page_edit.gif");
        editImg.setStyleName("icon-baseline");
        editImg.setTitle("Edit");
        editImg.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                showEditPanel();
            }
            
        });
        panel.add(editImg);
    }

    protected void showEditPanel() {
        panel.clear();

        final HorizontalPanel row = new HorizontalPanel();

        final SuggestBox workspaceSB = new SuggestBox(new ItemPathOracle(registryService, menuPanel));
        workspaceSB.setText(workspacePath);
        row.add(workspaceSB);
        
        row.add(new HTML("&nbsp;"));
        final ValidatableTextBox nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setText(name);

        row.add(nameTB);
        
        Button saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                if (!validateName(nameTB)) {
                    return;
                }
                save(workspaceSB.getText(), 
                     nameTB.getText());
            }
            
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                panel.clear();
                initName();
            }
            
        });

        row.add(saveButton);
        row.add(cancelButton);
        row.setVerticalAlignment(HasAlignment.ALIGN_TOP);
        panel.add(row);
    }

    protected void save(final String newParent, final String newName) {
        if (!newParent.equals(this.workspacePath) 
            || !newName.equals(this.name)) {
            // save only if name or workspace changed
            registryService.move(itemId, newParent, newName, new AbstractCallback(menuPanel) {

                @Override
                public void onCallFailure(Throwable caught) {
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
                    menuPanel.showPage(callbackParams);
                }

            });
        } else {
            // restore the original view
            panel.clear();
            initName();
        }
    }

    protected boolean validateName(final ValidatableTextBox nameTB) {
        boolean isOk = true;
        isOk &= nameTB.validate();

        return isOk;
    }
}
