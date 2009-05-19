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

package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WProperty;

/**
 * Shows all the artifact metadata.
 */
public class EntryMetadataPanel extends AbstractComposite {

    private FlowPanel metadata;
    private ErrorPanel errorPanel;
    private FlexTable table;
    private boolean showHidden = false;
    private Hyperlink showAll;
    private final Galaxy galaxy;
    private ItemInfo item;
    
    public EntryMetadataPanel(final Galaxy galaxy,
                              final ErrorPanel registryPanel,
                              final String title,
                              final ItemInfo item,
                              final boolean stale) {
        super();
        this.galaxy = galaxy;
        this.errorPanel = registryPanel;
        this.item = item;
        
        metadata = new FlowPanel();
        metadata.setStyleName("metadata-panel");
        
        table = createColumnTable();

        
        showAll = new Hyperlink("Show All", galaxy.getCurrentToken());
        showAll.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                table.clear();
                
                updateArtifactInfo();
            }
        });
        
        
        if (item.isModifiable()) {
            Hyperlink addMetadata = new Hyperlink("Add", galaxy.getCurrentToken());
            final EntryMetadataPanel amPanel = this;
            addMetadata.addClickListener(new ClickListener() {
    
                public void onClick(Widget arg0) {
                    NewPropertyPanel edit = new NewPropertyPanel(galaxy,
                                                                 errorPanel,
                                                                 galaxy.getRegistryService(),
                                                                 item.getId(),
                                                                 metadata,
                                                                 amPanel,
                                                                 table);
                    metadata.insert(edit, 1);   
                }
                
            });
            InlineFlowPanel metadataTitle = createTitleWithLink(title, asHorizontal(showAll, new Label(" "), addMetadata));
            metadata.add(metadataTitle);
        } else {
            metadata.add(createTitleWithLink(title, showAll));
        }

        initializeProperties(item.getProperties());
        
        if (stale) {
            metadata.add(new Label("NOTE: Indexed metadata for this artifact is currently in the process of being updated."));
        }
        metadata.add(table);
        initWidget(metadata);
    }

    protected void updateArtifactInfo() {
        showHidden  = !showHidden;
        
        if (showHidden) {
            showAll.setText("Show Summary");
        } else {
            showAll.setText("Show All");
        }
        
        RegistryServiceAsync svc = galaxy.getRegistryService();
        svc.getItemInfo(item.getId(), showHidden, new AbstractCallback<ItemInfo>(errorPanel) {

            public void onSuccess(ItemInfo o) {
                item = o;
                initializeProperties(item.getProperties());
            }
        });
    }

    private void initializeProperties(Collection<WProperty> properties) {
        for (WProperty p : properties) {
            addRow(p);
        }
    }
    
    public void addRow(WProperty property) {

        final AbstractPropertyRenderer renderer = 
            galaxy.getPropertyInterfaceManager().createRenderer(property.getExtension(), property.isMultiValued());

        EditPropertyPanel render = new EditPropertyPanel(renderer, errorPanel);
        render.setProperty(property);
        render.setGalaxy(galaxy);
        render.setItemId(item.getId());
        render.initialize();
        render.showView();
        
        addRow(property, render);
    }

    public void addRow(WProperty property, final EditPropertyPanel render) {
        int row = table.getRowCount();

        render.setDeleteListener(new ClickListener() {

            public void onClick(Widget arg0) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    Widget w = table.getWidget(i, 2);
                    
                    if (w.equals(render)) {
                        table.removeRow(i);
                        return;
                    }
                }
                item.getProperties().remove(render.getProperty());
            }
            
        });
        
        Label label = new Label(property.getDescription() + ":");
        label.setTitle(property.getName());
        table.setWidget(row, 0, label);
        
        if (property.isLocked()) {
            Image img = new Image("./images/lockedstate.gif");
            table.setWidget(row, 1, img);
        }
        
        table.setWidget(row, 2, render);
        table.getCellFormatter().setWidth(row, 0, "130px");
        table.getCellFormatter().setStyleName(row, 0, "artifactTableHeader");
        table.getCellFormatter().setStyleName(row, 1, "artifactTableLock");
        table.getCellFormatter().setStyleName(row, 2, "artifactTableEntry");
    }

    public boolean hasProperty(String name) {
        return item.getProperty(name) != null;
    }
    
}
