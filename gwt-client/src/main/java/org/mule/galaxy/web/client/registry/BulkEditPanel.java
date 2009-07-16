/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z merv $
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

package org.mule.galaxy.web.client.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.property.AbstractPropertyRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WPermission;
import org.mule.galaxy.web.rpc.WPolicyException;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class BulkEditPanel extends AbstractErrorShowingComposite
        implements ClickListener {

    private String query;

    private VerticalPanel wrapperPanel;
    private FlowPanel securityPanel;
    private FlowPanel propertyPanel;

    private Galaxy galaxy;
    private RegistryServiceAsync service;

    private CheckBox securityCB;
    private CheckBox setPropertyCB;
    private CheckBox delPropertyCB;

    private ListBox phaseLB;
    private ListBox setPropertyLB;
    private ListBox delPropertyLB;

    private Button save;
    private Button cancel;

    private Collection permissions;
    private Map groups;

    protected Collection propertyDescriptors;

    private FlexTable table;

    private AbstractPropertyRenderer renderer;

    private WPropertyDescriptor propertyDescriptor;

    private final long resultCount;

    private Collection<String> entryIds;

    public BulkEditPanel(Collection<String> entryIds, Galaxy galaxy) {
        this(entryIds.size(), galaxy);
        this.entryIds = entryIds;
    }
    
    public BulkEditPanel(String query, long resultCount, Galaxy galaxy) {
        this(resultCount, galaxy);
        this.query = query;
    }
    
    public BulkEditPanel(long resultCount, Galaxy galaxy) {
        this.resultCount = resultCount;
        this.galaxy = galaxy;
        this.service = galaxy.getRegistryService();
        this.permissions = new ArrayList();
        this.groups = new HashMap();

        // main wrapper panel for this edit screen
        this.wrapperPanel = new VerticalPanel();

        // subpanels for each editable section
        this.securityPanel = new FlowPanel();
        this.propertyPanel = new FlowPanel();

        // widgets
        this.securityCB = new CheckBox();
        this.setPropertyCB = new CheckBox();
        this.delPropertyCB = new CheckBox();
        this.phaseLB = new ListBox();
        this.setPropertyLB = new ListBox();
        setPropertyLB.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                onPropertySelect((ListBox)w);
            }
            
        });
        this.delPropertyLB = new ListBox();
        
        // main root panel
        initWidget(wrapperPanel);
    }


    /**
     * tell the left side menu panel to draw itself
     *
     * @param params
     */
    @Override
    public void show(List<String> params) {
        clearErrorMessage();
        this.doShow();
    }

    /**
     * main init method for this screen
     */
    @Override
    public void doShow() {

        wrapperPanel.clear();

        // by default they are all disabled
        phaseLB.setEnabled(false);
        delPropertyLB.setEnabled(false);
        //delPropertyTB.setEnabled(false);
        setPropertyLB.setEnabled(false);
        //setPropertyTB.setEnabled(false);

        Label label = new Label("Bulk Editing - " + resultCount + " Items");
        label.setStyleName("title");
        wrapperPanel.add(label);
        wrapperPanel.setStyleName("bulkedit-panel");

        // add whatever property panels we are allowed bulk edit
//        wrapperPanel.add(createSecurityPanel());
        wrapperPanel.add(createPropertyPanel());

        // save and cancel buttons
        save = new Button("Save");
        cancel = new Button("Cancel");
        save.addClickListener(this);
        cancel.addClickListener(this);
        wrapperPanel.add(asHorizontal(save, cancel));
    }


    /**
     * Too many anonymous inner classes will create a listener object overhead.
     * This will allow a single listener to distinguish between multiple event publishers.
     *
     * @param sender
     */
    public void onClick(Widget sender) {

        boolean checked = false;
        if (sender instanceof CheckBox) {
            checked = ((CheckBox) sender).isChecked();
        }

        if (sender == save) {
            save();

        } else if (sender == cancel) {
            cancel();

        } else if (sender == setPropertyCB) {
            setPropertyLB.setEnabled(checked);
        } else if (sender == delPropertyCB) {
            delPropertyLB.setEnabled(checked);
        }
    }

    /**
     * Configure property select boxes
     */
    private void updatePropertyListBox() {
        service.getPropertyDescriptors(false, new AbstractCallback(this) {
            public void onSuccess(Object result) {
                propertyDescriptors = (Collection) result;

                for (Iterator itr = propertyDescriptors.iterator(); itr.hasNext();) {
                    final WPropertyDescriptor prop = (WPropertyDescriptor) itr.next();
                    setPropertyLB.addItem(prop.getDescription(), prop.getId());
                    delPropertyLB.addItem(prop.getDescription(), prop.getId());
                }
                onPropertySelect(setPropertyLB);
            }
        });
    }
    
    protected void onPropertySelect(ListBox w) {
        int i = w.getSelectedIndex();
        if (i == -1) {
            return;
        }
        String txt = w.getValue(i);
        
        propertyDescriptor = getPropertyDescriptor(txt);

        renderer = 
            galaxy.getPropertyInterfaceManager().createRenderer(propertyDescriptor.getExtension(), 
                                                            propertyDescriptor.isMultiValued());
        renderer.initialize(galaxy, this, null, true);
        
        table.setWidget(0, 4, renderer.createEditForm());
    }


    private WPropertyDescriptor getPropertyDescriptor(String txt) {
        for (Iterator itr = propertyDescriptors.iterator(); itr.hasNext();) {
            WPropertyDescriptor pd = (WPropertyDescriptor) itr.next();
           
            if (txt.equals(pd.getName())) {
                return pd;
            }
        }
        return null;
    }
    private SimplePanel createTitlePanel(String title) {
        SimplePanel s = new SimplePanel();
        Label l = new Label(title);
        l.setStyleName("bulkedit-section-title");
        s.add(l);
        s.setStyleName("bulkedit-section-header");
        return s;
    }


    private FlexTable createItemTable() {
        FlexTable table = createTable();
        table.setStyleName("bulkedit-section-table");
        table.setCellSpacing(4);
        return table;
    }

    private FlowPanel createSecurityPanel() {
        FlexTable table = createItemTable();

        // get avaialable groups and permissions
        fetchPermissions();
        fetchGroups();

        securityPanel.add(createTitlePanel("Security"));
        securityCB.addClickListener(this);
        table.setWidget(0, 0, securityCB);

        // draw the colum headers
        int col = 1;
        for (Iterator itr = permissions.iterator(); itr.hasNext();) {
            WPermission p = (WPermission) itr.next();
            GWT.log(p.getDescription(), null);
            table.setText(0, col, p.getDescription());
            col++;
        }

        securityPanel.add(table);
        return securityPanel;
    }


    // RPC call to get list of available permissions
    private void fetchPermissions() {
        galaxy.getSecurityService().getPermissions(SecurityService.ITEM_PERMISSIONS, new AbstractCallback(this) {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                // FIXME: this returns empty?
                setPermissions((Collection)arg0);
            }
        });
    }


    private void setPermissions(Collection permissions) {
        this.permissions = permissions;

    }

    //RPC call to get list of available groups
    private void fetchGroups() {
        galaxy.getSecurityService().getGroupPermissions(new AbstractCallback(this) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                setGroups((Map) arg0);
            }

        });
    }

    private void setGroups(Map groups) {
        this.groups = groups;
    }


    protected Widget createGrantWidget() {
        ListBox lb = new ListBox();
        lb.addItem("Revoked");
        lb.addItem("Inherited");
        lb.addItem("Granted");
        return lb;
    }


    private FlowPanel createPropertyPanel() {
        table = createItemTable();

        setPropertyCB.addClickListener(this);
        delPropertyCB.addClickListener(this);

        propertyPanel.add(createTitlePanel("Set or Remove Property"));

        // init the available properties
        updatePropertyListBox();

        table.setWidget(0, 0, setPropertyCB);
        table.setText(0, 1, "Set: ");
        table.setWidget(0, 2, setPropertyLB);

        table.setText(0, 3, "Value: ");

        table.setWidget(1, 0, delPropertyCB);
        table.setText(1, 1, "Remove: ");
        table.setWidget(1, 2, delPropertyLB);
        
        propertyPanel.add(table);

        return propertyPanel;
    }

    // main save method for this class
    public void save() {
        save.setEnabled(false);
        if (securityCB.isChecked()) {
            saveSecurity();
        }
        if (setPropertyCB.isChecked()) {
            doSaveProperty();
        } else {
            deleteProperty();
        }

    }

    private void deleteProperty() {
        if (delPropertyCB.isChecked()) {
            deleteProperty(delPropertyLB.getValue(delPropertyLB.getSelectedIndex()));
        } else {
            finishRPCCalls();
        }
    }

    private void doSaveProperty() {
        Object value = renderer.getValueToSave();
        
        AbstractCallback callback = new AbstractCallback(this) {
            
            @Override
            public void onFailure(Throwable caught) {
                setEnabled(true);
                
                if (caught instanceof WPolicyException) {
                    WPolicyException pe = (WPolicyException) caught;

                    PolicyPanel.handlePolicyFailure(galaxy, pe);
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object arg0) {
                deleteProperty();
            }
        };
        
        RegistryServiceAsync svc = galaxy.getRegistryService();
        if (entryIds != null) {
            svc.setProperty(entryIds, propertyDescriptor.getName(), (Serializable)value, callback);
        } else {
            svc.setPropertyForQuery(query, propertyDescriptor.getName(), (Serializable)value, callback);
        }
    }


    public void cancel() {
        History.back();
    }
    
    // TODO:
    private void saveSecurity() {

    }
    
    private void deleteProperty(String name) {
        AbstractCallback callback = new AbstractCallback(this) {
            public void onFailure(Throwable caught) {
                setEnabled(true);

                if (caught instanceof WPolicyException) {
                    WPolicyException pe = (WPolicyException) caught;

                    PolicyPanel.handlePolicyFailure(galaxy, pe);
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object arg0) {
                finishRPCCalls();
            }
        };
        
        if (entryIds != null) {
            service.deleteProperty(entryIds, name, callback);
        } else {
            service.deletePropertyForQuery(query, name, callback);
        }
    }

    private void setEnabled(boolean enabled) {
        cancel.setEnabled(enabled);
        save.setEnabled(enabled);
    }
    
    private void finishRPCCalls() {
        setEnabled(true);
        History.newItem("browse");
    }


}
