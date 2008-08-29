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

package org.mule.galaxy.web.client.entry;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.WorkspaceOracle;
import org.mule.galaxy.web.client.util.WorkspacesListBox;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WWorkspace;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.List;

/**
 * A panel for editing the name of a registry entry.
 */
public class NameEditPanel extends Composite {

    private InlineFlowPanel panel;
    private final String versionId;
    private String name;
    private final String workspaceId;
    private final Galaxy galaxy;
    private final ErrorPanel errorPanel;

    private final EntryPanel callbackPanel;
    private final List<String> callbackParams;
    private final String version;

    public NameEditPanel(Galaxy galaxy,
                         ErrorPanel errorPanel,
                         String versionId,
                         String name,
                         String version,
                         String workspaceId, 
                         final EntryPanel callbackPanel, 
                         final List<String> callbackParams) {
        super();
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.versionId = versionId;
        this.name = name;
        this.version = version;
        this.workspaceId = workspaceId;

        panel = new InlineFlowPanel();

        this.callbackPanel = callbackPanel;
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

        final SuggestBox workspaceSB = new SuggestBox(new WorkspaceOracle(galaxy, errorPanel));
        row.add(workspaceSB);
        row.add(new HTML("&nbsp;"));
        final ValidatableTextBox nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setText(name);

        row.add(nameTB);
        
        final ValidatableTextBox versionTB = new ValidatableTextBox(new StringNotEmptyValidator());
        versionTB.getTextBox().setText(version);
        versionTB.getTextBox().setVisibleLength(5);
        
        row.add(versionTB);

        Button saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                if (!validateName(nameTB)) {
                    return;
                }
                save(workspaceSB.getText(), 
                     nameTB.getText(),
                     versionTB.getText());
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

    protected void save(final String newWorkspacePath, final String newName, String newVersion) {
        if (!newWorkspacePath.equals(this.workspaceId) 
            || !newName.equals(this.name)
            || !newVersion.equals(this.version)) {
            // save only if name or workspace changed
            galaxy.getRegistryService().move(versionId, newWorkspacePath, newName, newVersion, new AbstractCallback(errorPanel) {

                public void onSuccess(Object arg0) {
                    // need to refresh the whole panel to fetch new workspace location and entry name
                    callbackPanel.onShow(callbackParams);
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
