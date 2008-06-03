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

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.registry.RegistryMenuPanel;
import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.ExternalHyperlink;
import org.mule.galaxy.web.client.util.NavigationUtil;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.SecurityService;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;
import java.util.List;

import org.gwtwidgets.client.ui.LightBox;

/**
 * Contains:
 * - BasicArtifactInfo
 * - Service dependencies
 * - Depends on...
 * - Comments
 * - Governance tab
 *   (with history)
 * - View Artiact
 */
public class ArtifactPanel extends AbstractComposite {

    private Galaxy galaxy;
    private TabPanel artifactTabs;
    private ExtendedArtifactInfo info;
    private ArtifactGroup group;
    private VerticalPanel panel;
    private int selectedTab = -1;
    private ListBox versionLB;
    private RegistryMenuPanel menuPanel;
    protected FlowPanel linkBox;

    public ArtifactPanel(Galaxy galaxy) {
        this.galaxy = galaxy;
        
        menuPanel = new RegistryMenuPanel(galaxy) {

            protected void addTopLinks(Toolbox topMenuLinks) {
                if (linkBox == null) {
                    linkBox = new FlowPanel();
                }
                topMenuLinks.add(linkBox);
                topMenuLinks.add(new Label(" "));
            }
            
        };
        
        panel = new VerticalPanel();
        panel.setWidth("100%");
        menuPanel.setMain(panel);

        
        initWidget(menuPanel);
    }
    
    public void onShow(List params) {
        menuPanel.clearErrorMessage();
        menuPanel.onShow();
        panel.clear();
        panel.add(new Label("Loading..."));
        
        String artifactId = (String) params.get(0);
        if (params.size() >= 2) {
            selectedTab = new Integer((String)params.get(1)).intValue();
        } else {
            selectedTab = 0;
        }
        
        galaxy.getRegistryService().getArtifact(artifactId, new AbstractCallback(menuPanel) { 
            public void onSuccess(Object o) {
                group = (ArtifactGroup) o;
                info = (ExtendedArtifactInfo) group.getRows().get(0);
                
                init();
            }
        });
    }

    private void init() {
        panel.clear();
        artifactTabs = new TabPanel();
        artifactTabs.setStyleName("artifactTabPanel");
        artifactTabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        panel.add(artifactTabs);
        
        FlowPanel artifactTitle = new FlowPanel();
        artifactTitle.setStyleName("artifact-title-base");
        artifactTitle.add(newLabel(info.getPath(), "artifact-path"));
        
        FlexTable titleTable = new FlexTable();
        titleTable.setStyleName("artifact-title");
        titleTable.setWidget(0, 0, newLabel(info.getName(), "artifact-name"));
        
        ArtifactVersionInfo defaultVersion = null;
        versionLB = new ListBox();
        for (Iterator itr = info.getVersions().iterator(); itr.hasNext();) {
            ArtifactVersionInfo v = (ArtifactVersionInfo)itr.next();
            
            versionLB.addItem(v.getVersionLabel(), v.getId());
            if (v.isDefault()) {
                defaultVersion = v;
                versionLB.setSelectedIndex(versionLB.getItemCount()-1);
            }
        }
        versionLB.addChangeListener(new ChangeListener() {

            public void onChange(Widget arg0) {
                viewNewVersion();
            }
            
        });
        titleTable.setWidget(0, 1, versionLB);
        
        Image img = new Image("images/feed-icon-14x14.png");
        img.setStyleName("feed-icon");
        img.setTitle("Versions Atom Feed");
        img.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                Window.open(info.getArtifactFeedLink(), null, "scrollbars=yes");
            }
            
        });
        titleTable.setWidget(0, 2, img);
        
        artifactTitle.add(titleTable);
        
        panel.insert(artifactTitle, 0);
        
        initTabs(defaultVersion);
    }

    protected void viewNewVersion() {
        int idx = versionLB.getSelectedIndex();
        String id = versionLB.getValue(idx);
        
        for (Iterator itr = info.getVersions().iterator(); itr.hasNext();) {
            ArtifactVersionInfo avi = (ArtifactVersionInfo)itr.next();               
            
            if (avi.getId().equals(id)) {
                artifactTabs.clear();
                initTabs(avi);
                return;
            }
        }
    }

    private void initTabs(ArtifactVersionInfo version) {
        artifactTabs.add(new ArtifactInfoPanel(galaxy, menuPanel, group, info, version), "Info");
        artifactTabs.add(new GovernancePanel(galaxy, menuPanel, version), "Governance");
        artifactTabs.add(new HistoryPanel(galaxy, menuPanel, info), "History");
        if (galaxy.hasPermission("MANAGE_GROUPS")) {
            artifactTabs.add(new ItemGroupPermissionPanel(galaxy, menuPanel, info.getId(), SecurityService.ARTIFACT_PERMISSIONS), "Security");
        }
        
        if (selectedTab > -1) {
            artifactTabs.selectTab(selectedTab);
        } else {
            artifactTabs.selectTab(0);
        }

        artifactTabs.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents arg0, int arg1) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents events, int tab) {
                menuPanel.clearErrorMessage();
                AbstractComposite composite = (AbstractComposite) artifactTabs.getWidget(tab);
                
                composite.onShow();
            }
            
        });

        linkBox.clear();

        ClickListener cl = new ClickListener() {

            public void onClick(Widget arg0) {
                Window.open(info.getArtifactLink(), null, "scrollbars=yes");
            }
            
        };
        Image img = new Image("images/external.png");
        img.setStyleName("viewArtifactImage");
        img.addClickListener(cl);

        Hyperlink hl = new Hyperlink("View Artifact", "artifact_" + info.getId());
        hl.addClickListener(cl);
        linkBox.add(asHorizontal(img, new Label(" "), hl));

        ExternalHyperlink permalink = new ExternalHyperlink("Permalink", info.getArtifactLink());
        permalink.setTitle("Direct artifact link for inclusion in email, etc.");
        linkBox.add(asHorizontal(new Image("images/permalink.gif"), new Label(" "), permalink));
        
        String token = "new-artifact-version_" + info.getId();

        img = new Image("images/new-version.gif");
        img.addClickListener(NavigationUtil.createNavigatingClickListener(token));
        hl = new Hyperlink("New Version", token);
        linkBox.add(asHorizontal(img, new Label(" "), hl));
        
        cl = new ClickListener() {
            public void onClick(Widget arg0) {
                warnDelete();
            }
        };
        
        img = new Image("images/delete_config.gif");
        img.addClickListener(cl);
        hl = new Hyperlink("Delete", "artifact_" + info.getId());
        hl.addClickListener(cl);
        linkBox.add(asHorizontal(img, new Label(" "), hl));
    }
    
    protected void warnDelete()
    {
        new LightBox(new ConfirmDialog(new ConfirmDialogAdapter()
        {
            public void onConfirm()
            {
                galaxy.getRegistryService().delete(info.getId(), new AbstractCallback(menuPanel)
                {
                    public void onSuccess(Object arg0)
                    {
                        galaxy.setMessageAndGoto("browse", "Artifact was deleted.");
                    }
                });
            }
        }, "Are you sure you want to delete this artifact?")).show();
    }


}
