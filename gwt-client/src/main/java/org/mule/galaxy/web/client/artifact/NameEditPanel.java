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
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NameEditPanel extends Composite {

    /**
     * A simple map of input field -> validation listener for UI updates.
     */
    private Map/*<Widget, ValidationListener>*/ validationListeners = new HashMap();

    private InlineFlowPanel panel;
    private final String artifactId;
    private final String name;
    private final String workspaceId;
    private final Galaxy galaxy;
    private final ErrorPanel errorPanel;

    public NameEditPanel(Galaxy galaxy, 
                         ErrorPanel errorPanel, 
                         String artifactId, 
                         String name, 
                         String workspaceId) {
        super();
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.artifactId = artifactId;
        this.name = name;
        this.workspaceId = workspaceId;

        panel = new InlineFlowPanel();
       
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
        panel.add(workspacesLB);
        panel.add(new Label(" "));
        final   TextBox nameTB = new TextBox();
        nameTB.setText(name);
        panel.add(nameTB);
        
        Button saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                save(workspacesLB.getSelectedValue(), nameTB.getText());
            }
            
        });
        panel.add(saveButton);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                panel.clear();
                initName();
            }
            
        });
        panel.add(cancelButton);
    }

    protected void save(String workspaceId, String name) {
        galaxy.getRegistryService().move(artifactId, workspaceId, name, new AbstractCallback(errorPanel) {

            public void onSuccess(Object arg0) {
                panel.clear();
                initName();
            }
            
        });
    }

}
