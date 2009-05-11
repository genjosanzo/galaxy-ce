/*
 * $Id$
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

package org.mule.galaxy.web.client.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.LightBox;
import org.mule.galaxy.web.client.util.PropertyDescriptorComparator;
import org.mule.galaxy.web.client.util.StylizedSortableGrid;
import org.mule.galaxy.web.client.util.WTypeComparator;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.rpc.WType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.SortableGrid;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class TypeForm extends AbstractAdministrationForm {

    private WType type;
    private ValidatableTextBox nameTB;
    private ListBox mixinsLB;
    private ListBox childrenLB;
    private Map<String, WType> types = new HashMap<String, WType>();
    private FlowPanel propertiesPanel;
    private ListBox globalPropertiesLB;
    private List<WPropertyDescriptor> globalProperties;
    private SortableGrid propertiesTable;
    private InnerPropertyDescriptorForm innerForm;
    
    public TypeForm(AdministrationPanel adminPanel){
        super(adminPanel, "types", "Type was saved.", "Type was deleted.", 
              "A type with that name already exists.");
    }
    
    protected void addFields(final FlexTable table) {
        table.setText(0, 0, "Name:");
        table.setText(1, 0, "Properties:");
        table.setText(2, 0, "Mixins:");
        table.setText(3, 0, "Allowed Children:");
        
        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setText(type.getName());

        table.setWidget(0, 1, nameTB);
        
        propertiesPanel = new InlineFlowPanel();
        propertiesPanel.add(initializeTypeProperties());
        propertiesPanel.add(newSpacer());
        
        globalPropertiesLB = new ListBox();
        
        Button addBtn = new Button("Add");
        addBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                addGlobalProperty();
            }
        });

        Button newBtn = new Button("New");
        newBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                showPropertyForm(null);
            }
        });
        
        final Button editBtn = new Button("Edit");
        editBtn.setEnabled(false);
        editBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                showPropertyForm(getSelectedProperty());
            }
        });
        
        
        final Button removeBtn = new Button("Remove");
        removeBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                removeProperty();
            }
        });
        
        propertiesTable.addRowSelectionHandler(new RowSelectionHandler() {
            public void onRowSelection(RowSelectionEvent event) {
                if (event.getSelectedRows() != null && event.getSelectedRows().size() > 0) {
                    removeBtn.setEnabled(true);
                    editBtn.setEnabled(true);
                } else if (event.getDeselectedRows() != null && event.getDeselectedRows().size() > 0) {
                    removeBtn.setEnabled(false);
                    editBtn.setEnabled(true);
                }
            }
            
        });
        
        FlowPanel btnPanel = new FlowPanel();
        btnPanel.add(asHorizontal(new Label("Global Properties: "), globalPropertiesLB, addBtn));
        btnPanel.add(newBtn);        
        btnPanel.add(editBtn);        
        btnPanel.add(removeBtn);
        
        propertiesPanel.add(btnPanel);
        
        table.setWidget(1, 1, propertiesPanel);
        
        mixinsLB = new ListBox(true);
        mixinsLB.setVisibleItemCount(6);
        table.setWidget(2, 1, mixinsLB);

        childrenLB = new ListBox(true);
        childrenLB.setVisibleItemCount(6);
        table.setWidget(3, 1, childrenLB);
        
        styleHeaderColumn(table);
        
        intializeTypesAndProperties();
    }

    protected WPropertyDescriptor getSelectedProperty() {
        int row = getSelectedRow();
        
        if (row == -1) {
            return null;
        }
        
        String name = propertiesTable.getText(row, 1);
        WPropertyDescriptor w = null;
        for (WPropertyDescriptor pd : type.getProperties()) {
            if (pd.getName().equals(name)) {
                w = pd;
                break;
            }
        }
        
        return w;
    }

    private Integer getSelectedRow() {
        int row = -1;
        for (Integer i : propertiesTable.getSelectedRows()) {
            row = i;
            break;
        }
        return row;
    }

    /**
     * Bring up a dialog form to edit a property.
     * @param p
     */
    protected void showPropertyForm(WPropertyDescriptor p) {
        final boolean add = p == null;
        
        final DialogBox panel = new DialogBox();
        if (add) {
            p = new WPropertyDescriptor();
            panel.setText("Add Property");
        } else {
            panel.setText("Edit Property " + p.getDescription());
        }
        
        FlowPanel container = new FlowPanel();
        FlexTable table = new FlexTable();
        container.add(table);
        this.innerForm = new InnerPropertyDescriptorForm();
        innerForm.initialize(galaxy, p, table);
        
        Button cancel = new Button("Cancel");
        cancel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                panel.hide();
            }
        });
        
        Button save = new Button("Save");
        save.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if (!innerForm.validate()) {
                    return;
                }
                
                WPropertyDescriptor pd = innerForm.getPropertyDescriptor();
                
                if (add) {
                    pd.setTypeId("new");
                    addTypeProperty(pd);
                    type.addProperty(pd);
                } else {
                    displayProperty(pd, getSelectedRow());
                }
                
                panel.hide();
            }
        });
        
        container.add(asHorizontal(cancel, save));
        
        panel.add(container);
        new LightBox(panel).show();
    }

    protected void addGlobalProperty() {
        int idx = globalPropertiesLB.getSelectedIndex();
        String selected = globalPropertiesLB.getValue(idx);
        
        for (WPropertyDescriptor pd : globalProperties) {
            if (selected.equals(pd.getId())) {
                addTypeProperty(pd);
                type.addProperty(pd);
                globalPropertiesLB.removeItem(idx);
            }
        }
    }

    protected void fetchItem(String id) {
        adminPanel.getRegistryService().getType(id, getFetchCallback());
    }

    protected void initializeProperties(List<WPropertyDescriptor> pds) {
        Collections.sort(pds, new PropertyDescriptorComparator());
        this.globalProperties = pds;
        
        for (WPropertyDescriptor pd : pds) {
            if (type.getProperties() == null || !type.getProperties().contains(pd)) {
                globalPropertiesLB.addItem(pd.getDescription(), pd.getId());
            }
        }
    }
    
    /**
     * Draw the initial properties table;
     * @return
     */
    protected SortableGrid initializeTypeProperties() {
        propertiesTable = new StylizedSortableGrid(1, 4);
        
        createSortHeader(0, 0, "Description", propertiesTable);
        createSortHeader(0, 1, "Name", propertiesTable);
        createSortHeader(0, 2, "Type", propertiesTable);
        createSortHeader(0, 3, "Global", propertiesTable);
        
        int row = 1;
        if (type.getProperties() != null) {
            for (WPropertyDescriptor pd : type.getProperties()) {
                addTypeProperty(pd);
                row++;
            }
        }
        return propertiesTable;
    }

    protected void addTypeProperty(WPropertyDescriptor pd) {
        int row = propertiesTable.getRowCount() + 1;
        propertiesTable.resizeRows(row);
        
        displayProperty(pd, row - 1);
    }

    private void displayProperty(WPropertyDescriptor pd, int row) {
        propertiesTable.setText(row, 0, pd.getDescription());
        propertiesTable.setText(row, 1, pd.getName());
        
        String ext = pd.getExtension();
        String type;
        if ("linkExtension".equals(ext)) {
            type = "Link";
        } else if ("lifecycleExtension".equals(ext)) {
            type = "Lifecycle";
        } else if ("userExtension".equals(ext)) {
            type = "User";
        } else if ("artifactExtension".equals(ext)) {
            type = "File";
        } else if ("mapExtension".equals(ext)) {
            type = "Map";
        } else {
            type = "String";
        }

        if (pd.isMultiValued()) type += "s";

        propertiesTable.setText(row, 2, type);
        propertiesTable.setText(row, 3, pd.getTypeId() == null ? "Yes" : "No");
    }

    private void createSortHeader(final int row, 
                                  final int col, 
                                  final String header, 
                                  final SortableGrid propertiesTable) { 
        final Hyperlink sortLink = new Hyperlink(header, "nohistory");
        sortLink.addClickHandler(new ClickHandler() {
            private boolean ascending = true;
            public void onClick(ClickEvent arg0) {
                sortLink.setTargetHistoryToken(History.getToken());
                propertiesTable.sortColumn(col, ascending);
//                if (ascending) {
//                    sortLink.setText(header + " a");
//                } else {
//                    sortLink.setText(header + " d");
//                }
                ascending = !ascending;
            }
        });
        
        propertiesTable.setWidget(row, col, sortLink);
        propertiesTable.getCellFormatter().setStyleName(row, col, "SortableGrid-header");
    }

    protected void initializeTypes(List<WType> wts) {
        Collections.sort(wts, new WTypeComparator());
        for (WType type : wts) {
            types.put(type.getId(), type);
            mixinsLB.addItem(type.getName(), type.getId());
            if (this.type.getMixinIds().contains(type.getId())) {
                mixinsLB.setItemSelected(mixinsLB.getItemCount()-1, true);
            }
            childrenLB.addItem(type.getName(), type.getId());
            if (this.type.getAllowedChildrenIds().contains(type.getId())) {
                childrenLB.setItemSelected(mixinsLB.getItemCount()-1, true);
            }
        }
    }

    public String getTitle() {
        if (newItem) {
            return "Add Type";
        } else {
            return "Edit Type Descriptor: " + type.getName();
        }
    }

    protected void initializeItem(Object o) {
        this.type = (WType) o;    }

    protected void initializeNewItem() {
        this.type = new WType();
    }

    protected void intializeTypesAndProperties() {
        adminPanel.getRegistryService().getTypes(new AbstractCallback<List<WType>>(errorPanel) {
            public void onSuccess(List<WType> types) {
                initializeTypes(types);
            }

            @Override
            public void onFailure(Throwable caught) {
                errorPanel.setMessage(caught.getClass().getName());
                super.onFailure(caught);
            }
        });

        adminPanel.getRegistryService().getPropertyDescriptors(false, new AbstractCallback(errorPanel) {
            public void onSuccess(Object pds) {
                initializeProperties((List<WPropertyDescriptor>) pds);
            }

            @Override
            public void onFailure(Throwable caught) {
                errorPanel.setMessage(caught.getClass().getName());
                super.onFailure(caught);
            }
        });
    }
    
    protected void save() {

        if (!validate()) {
            return;
        }

        RegistryServiceAsync svc = adminPanel.getRegistryService();

        type.setName(nameTB.getTextBox().getText());
        type.setAllowedChildrenIds(new ArrayList<String>());
        type.setMixinIds(new ArrayList<String>());
        
//        for (int i = 0; i < propertiesTable.getItemCount(); i++) {
//            if (propertiesTable.isItemSelected(i)) {
//                type.getProperties().add(properties.get(propertiesTable.getValue(i)));
//            }
//        }
        
        for (int i = 0; i < childrenLB.getItemCount(); i++) {
            if (childrenLB.isItemSelected(i)) {
                type.getAllowedChildrenIds().add(childrenLB.getValue(i));
            }
        }

        for (int i = 0; i < mixinsLB.getItemCount(); i++) {
            if (mixinsLB.isItemSelected(i)) {
                type.getMixinIds().add(mixinsLB.getValue(i));
            }
        }
        svc.saveType(type, getSaveCallback());
    }

    protected void delete() {
        final ConfirmDialog dialog = new ConfirmDialog(new ConfirmDialogAdapter() {
            public void onConfirm() {
                TypeForm.super.delete();
                RegistryServiceAsync svc = adminPanel.getRegistryService();
                svc.deletePropertyDescriptor(type.getId(), getDeleteCallback());
            }
        }, "Are you sure you want to delete type " + type.getName() + "?");
        new LightBox(dialog).show();
    }

    protected boolean validate() {
        boolean isOk = true;

        isOk &= nameTB.validate();
        
        return isOk;
    }

    protected void removeProperty() {
        // TODO: warn that the property on these types will be deleted
        
        WPropertyDescriptor p = getSelectedProperty();
        type.getProperties().remove(p);
        
        for (int i : propertiesTable.getSelectedRows()) {
            propertiesTable.removeRow(i);
        }
    }

}
