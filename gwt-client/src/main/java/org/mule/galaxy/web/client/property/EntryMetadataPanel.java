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

import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WProperty;

public class EntryMetadataPanel extends AbstractComposite {

    private FlowPanel metadata;
    private ErrorPanel errorPanel;
    private ArtifactVersionInfo info;
    private FlexTable table;
    private boolean showHidden = false;
    private Hyperlink showAll;
    private final Galaxy galaxy;
    
    public EntryMetadataPanel(final Galaxy galaxy,
                                 final ErrorPanel registryPanel,
                                 final ExtendedArtifactInfo artifactInfo,
                                 final ArtifactVersionInfo info) {
        super();
        this.galaxy = galaxy;
        this.info = info;
        this.errorPanel = registryPanel;
        
        metadata = new FlowPanel();
        metadata.setStyleName("metadata-panel");
        
        table = createColumnTable();
        
        Hyperlink addMetadata = new Hyperlink("Add", "no-history");
        final EntryMetadataPanel amPanel = this;
        addMetadata.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                NewPropertyPanel edit = new NewPropertyPanel(galaxy,
                                                             errorPanel,
                                                             galaxy.getRegistryService(),
                                                             info.getId(),
                                                             metadata,
                                                             amPanel,
                                                             table);
                metadata.add(edit);   
            }
            
        });

        showAll = new Hyperlink("Show All", "no-history");
        showAll.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                table.clear();
                
                updateArtifactInfo();
            }
        });
        
        InlineFlowPanel metadataTitle = createTitleWithLink("Metadata", asHorizontal(showAll, new Label(" "), addMetadata));
        metadata.add(metadataTitle);

        initializeProperties(info);
        
        if (info.isIndexInformationStale()) {
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
        svc.getArtifactVersionInfo(info.getId(), showHidden, new AbstractCallback(errorPanel) {

            public void onSuccess(Object o) {
                info = (ArtifactVersionInfo) o;
                
                initializeProperties(info);
            }
        });
    }

    private void initializeProperties(final ArtifactVersionInfo info) {
        for (Iterator itr = info.getProperties().iterator(); itr.hasNext();) {
            WProperty p = (WProperty) itr.next();
            
            
            addRow(p);
        }
    }
    
    public void addRow(WProperty property) {

        final PropertyPanel render = galaxy.getPropertyPanelFactory().createRenderer(property.getExtension(), 
                                                                                     property.isMultiValued());

        render.setProperty(property);
        render.setGalaxy(galaxy);
        render.setItemId(info.getId());
        render.initialize();
        render.showView();
        
        addRow(property, render);
    }

    public void addRow(WProperty property, final PropertyPanel render) {
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
    
}
