package org.mule.galaxy.web.client.artifact;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WProperty;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

public class ArtifactMetadataPanel extends AbstractComposite {

    private FlowPanel metadata;
    private RegistryPanel registryPanel;
    private ArtifactVersionInfo info;
    private FlexTable table;
    private boolean showHidden = false;
    private Hyperlink showAll;
    
    public ArtifactMetadataPanel(final RegistryPanel registryPanel,
                                 final ExtendedArtifactInfo artifactInfo,
                                 final ArtifactVersionInfo info) {
        super();
        this.info = info;
        this.registryPanel = registryPanel;
        
        metadata = new FlowPanel();
        metadata.setStyleName("metadata-panel");
        
        table = createColumnTable();
        
        Hyperlink addMetadata = new Hyperlink("Add", "no-history");
        final ArtifactMetadataPanel amPanel = this;
        addMetadata.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                PropertyEditPanel edit = new PropertyEditPanel(registryPanel, 
                                                               artifactInfo.getId(),
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
        RegistryServiceAsync svc = registryPanel.getRegistryService();
        svc.getArtifactVersionInfo(info.getId(), showHidden, new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                info = (ArtifactVersionInfo) o;
                
                initializeProperties(info);
            }
        });
    }

    private void initializeProperties(final ArtifactVersionInfo info) {
        int i = 0;
        for (Iterator itr = info.getProperties().iterator(); itr.hasNext();) {
            WProperty p = (WProperty) itr.next();
            
            createPropertyRow(i, p);
            
            i++;
        }
    }
    
    private void createPropertyRow(final int row, 
                                   final WProperty p)
    {
        Label label = new Label(p.getDescription() + ":");
        label.setTitle(p.getName());
        table.setWidget(row, 0, label);
        final String name = p.getName();
        final String value = p.getValue();
        final boolean locked = p.isLocked();

        setRow(row, name, value, locked);

    }

    private void setRow(final int row, final String name, final String value, final boolean locked) {
        String txt = value;
        Widget w;
        if (locked) {
            if ("".equals(txt) || txt == null) {
                txt = "-----";
            }
            w = new Label(txt);
            Image img = new Image("./images/lockedstate.gif");
            table.setWidget(row, 1, img);
        } else {
            txt += " ";
            Hyperlink editHL = new Hyperlink("Edit", "edit-property");
            editHL.setStyleName("propertyLink");
            editHL.addClickListener(new ClickListener() {

                public void onClick(Widget widget) {
                    edit(name, value, row);
                 }
                
            });
            
            Hyperlink deleteHL = new Hyperlink("Delete", "delete-property");
            deleteHL.setStyleName("propertyLink");
            deleteHL.addClickListener(new ClickListener() {

                public void onClick(Widget widget) {
                   delete(name, row);
                }
                
            });
            
            InlineFlowPanel valuePanel = new InlineFlowPanel();
            valuePanel.add(new Label(txt));
            valuePanel.add(editHL);
            valuePanel.add(deleteHL);
            w = valuePanel;
        }
        
        table.setWidget(row, 2, w);
        table.getCellFormatter().setWidth(row, 0, "130px");
        table.getCellFormatter().setStyleName(row, 0, "artifactTableHeader");
        table.getCellFormatter().setStyleName(row, 1, "artifactTableLock");
        table.getCellFormatter().setStyleName(row, 2, "artifactTableEntry");
    }


    protected void edit(final String name, final String value, final int row) {
        InlineFlowPanel editPanel = new InlineFlowPanel();
        
        final TextBox valueTB = new TextBox();
        valueTB.setText(value);
        valueTB.setVisibleLength(50);
        editPanel.add(valueTB);
        
        final Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                table.clearCell(row, 2);
                setRow(row, name, value, false);
            }
            
        });
        
        final Button save = new Button("Save");
        save.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                cancel.setEnabled(false);
                save.setEnabled(false);
                
                save(name, valueTB.getText(), row, cancel, save);
            }
            
        });
        editPanel.add(cancel);
        editPanel.add(save);
        
        table.setWidget(row, 2, editPanel);
    }

    protected void save(final String name, final String value, final int row, 
                        final Button cancel, final Button save) {
        registryPanel.getRegistryService().setProperty(info.getId(), name, value, new AbstractCallback(registryPanel) {

            public void onFailure(Throwable caught) {
                cancel.setEnabled(true);
                save.setEnabled(true);
                
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                table.clearCell(row, 2);
                setRow(row, name, value, false);
            }
            
        });
    }


    protected void delete(String name, final int row) {
        registryPanel.getRegistryService().deleteProperty(info.getId(), name, new AbstractCallback(registryPanel) {

            public void onSuccess(Object arg0) {
                table.removeRow(row);
            }
            
        });
    }

    public void addProperty(String name, String desc, String value) {
        int rows = table.getRowCount();

        Label label = new Label(desc);
        label.setTitle(name);
        table.setWidget(rows, 0, label);
        
        setRow(rows, name, value, false);
    }
}
