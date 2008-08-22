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

package org.mule.galaxy.web.client.workspace;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.entry.ItemGroupPermissionPanel;
import org.mule.galaxy.web.client.registry.RegistryMenuPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WWorkspace;

public class ManageWorkspacePanel extends AbstractErrorShowingComposite {

    private FlowPanel panel;
    private String workspaceId;
    private final Galaxy galaxy;
    private RegistryMenuPanel menuPanel;

    public ManageWorkspacePanel(final Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
        
        menuPanel = new RegistryMenuPanel(galaxy);
        
        panel = new FlowPanel();
        menuPanel.setMain(panel);
        
        initWidget(menuPanel);
    }

    public void onShow(List<String> params) {
        panel.clear();
        panel.add(new Label("Loading..."));
        
        if (params.size() > 0) {
            workspaceId = params.get(0);
        }

        menuPanel.onShow();
        
        galaxy.getRegistryService().getWorkspace(workspaceId, new AbstractCallback<WWorkspace>(this) {
            public void onSuccess(WWorkspace i) {
                loadForm(i);
            }
        });
    }
    
    public void loadForm(WWorkspace workspace) {
        panel.clear();

        panel.add(createPrimaryTitle("Manage Workspace - " + workspace.getName()));
        
        final TabPanel tabs = new TabPanel();
        panel.add(tabs);
        
        tabs.setStyleName("artifactTabPanel");
        tabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        tabs.add(new WorkspaceForm(galaxy, workspace), "Info");
        tabs.add(new PolicyPanel(this, galaxy, workspaceId), "Governance");
        if (galaxy.hasPermission("MANAGE_GROUPS")) {
            tabs.add(new ItemGroupPermissionPanel(galaxy, 
                                                  this,
                                                  workspaceId,
                                                  SecurityService.WORKSPACE_PERMISSIONS), "Security");
        }
        
        tabs.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents arg0, int arg1) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents events, int tab) {
                AbstractComposite composite = (AbstractComposite) tabs.getWidget(tab);
                
                composite.onShow();
            }
            
        });
        tabs.selectTab(0);
        AbstractComposite composite = (AbstractComposite) tabs.getWidget(0);
        List<String> args = Collections.emptyList();
        composite.onShow(args);
    }
    

}
