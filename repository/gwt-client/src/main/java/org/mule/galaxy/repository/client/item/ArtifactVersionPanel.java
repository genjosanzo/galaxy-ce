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

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.client.ui.ExternalHyperlink;
import org.mule.galaxy.web.client.ui.panel.WidgetHelper;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

public class ArtifactVersionPanel extends LayoutContainer {
    
    private final ItemInfo info;
    private RegistryServiceAsync registryService;
    private final RepositoryMenuPanel menuPanel;
    private ContentPanel contentPanel;

    public ArtifactVersionPanel(final RepositoryMenuPanel menuPanel,
                         final ItemInfo info, 
                         final ItemPanel artifactPanel, 
                         final List<String> callbackParams) {
        setBorders(false);
        
        this.menuPanel = menuPanel;
        this.registryService = menuPanel.getRepositoryModule().getRegistryService();
        
        this.info = info;
        
        setId("artifactVersionPanel");

        contentPanel = new ContentPanel();
        contentPanel.setAutoHeight(true);
        contentPanel.setAutoWidth(true);
        contentPanel.setBodyBorder(false);
        contentPanel.addStyleName("x-panel-container-full");
        
        Image editImg = new Image("images/page_edit.gif");
        editImg.setStyleName("icon-baseline");
        editImg.setTitle("Edit");
        editImg.addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent arg0) {
                Window window = new Window() {

                    @Override
                    public void hide() {
                        ModelData selected = menuPanel.getTree().getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            selected.set("name", info.getName());
                        }

                        // refresh everything
                        super.hide();
                    }
                    
                };
                window.add(new NameEditPanel(menuPanel, info, false, window));
                window.show();
            }
        });
        contentPanel.getHeader().addTool(new WidgetComponent(editImg));
        

        Image downloadImg = new Image("images/icon_download.gif");
        downloadImg.setStyleName("icon-baseline");
        downloadImg.setTitle("Download");
        downloadImg.addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent arg0) {
                com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL() +"../api/registry" + info.getParentPath() + "?version=" + info.getName(), 
                            "123", ""); 
            }
        });
        contentPanel.getHeader().addTool(new WidgetComponent(downloadImg));
        
        contentPanel.setHeading(info.getName());
        
        TableLayout layout = new TableLayout(2);
        layout.setCellSpacing(6);
        LayoutContainer avInfo = new LayoutContainer(layout);
        avInfo.add(WidgetHelper.columnLabel("Artifact:"));
        avInfo.add(new Label(info.getParentName()));
        avInfo.add(WidgetHelper.columnLabel("Creator:"));
        avInfo.add(new Label(info.getAuthorName()));
        contentPanel.add(avInfo);
        
        add(contentPanel);
        
    }

}
