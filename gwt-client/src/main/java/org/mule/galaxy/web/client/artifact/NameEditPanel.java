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

package org.mule.galaxy.web.client.artifact;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.WorkspacesListBox;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.List;

/**
 * A panel for editing the name of a registry entry.
 */
public class NameEditPanel extends Composite {

    private InlineFlowPanel panel;
    private final String artifactId;
    private String name;
    private final String workspaceId;
    private final Galaxy galaxy;
    private final ErrorPanel errorPanel;

    private final ArtifactPanel callbackPanel;
    private final List callbackParams;

    public NameEditPanel(Galaxy galaxy,
                         ErrorPanel errorPanel,
                         String artifactId,
                         String name,
                         String workspaceId, final ArtifactPanel callbackPanel, final List callbackParams) {
        super();
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.artifactId = artifactId;
        this.name = name;
        this.workspaceId = workspaceId;

        panel = new InlineFlowPanel();

        this.callbackPanel = callbackPanel;
        this.callbackParams = callbackParams;

        initName();
        
        initWidget(panel);
    }

    private void initName() {
        panel.add(new Label(name + " "));

        Hyperlink editHL = new Hyperlink("Edit", "edit-property");
        editHL.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                showEditPanel();
            }
            
        });
        panel.add(editHL);
    }

    protected void showEditPanel() {
        panel.clear();
        panel.add(new Label("Loading workspaces..."));
        
        galaxy.getRegistryService().getWorkspaces(new AbstractCallback(errorPanel) {
            public void onSuccess(Object workspaces) {
                showEditPanel((Collection) workspaces);
            }
        });
    }

    protected void showEditPanel(Collection workspaces) {
        panel.clear();
        
        
        final WorkspacesListBox workspacesLB = new WorkspacesListBox(workspaces, 
                                                                     null,
                                                                     workspaceId,
                                                                     false);
        final HorizontalPanel row = new HorizontalPanel();

        row.add(workspacesLB);
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
                save(workspacesLB.getSelectedValue(), nameTB.getTextBox().getText());
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

    protected void save(final String newWorkspaceId, final String newName) {
        if (!newWorkspaceId.equals(this.workspaceId) || !newName.equals(this.name)) {
            // save only if name or workspace changed
            galaxy.getRegistryService().move(artifactId, newWorkspaceId, newName, new AbstractCallback(errorPanel) {

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
