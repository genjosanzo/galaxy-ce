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

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class ArtifactBulkEditPanel extends AbstractErrorShowingComposite
        implements ClickListener, ChangeListener {

    private Collection artifactIds;
    private Collection lifecycles;

    private VerticalPanel wrapperPanel;
    private FlowPanel lifecyclePanel;
    private FlowPanel securityPanel;
    private FlowPanel propertyPanel;

    private Galaxy galaxy;
    private RegistryMenuPanel menuPanel;
    private RegistryServiceAsync service;

    private CheckBox securityCB;
    private CheckBox lifecycleCB;
    private CheckBox setPropertyCB;
    private CheckBox delPropertyCB;

    private ListBox lifecycleLB;
    private ListBox phaseLB;
    private ListBox setPropertyLB;
    private ListBox delPropertyLB;

    private Button save;
    private Button cancel;
    private TextBox setPropertyTB;


    public ArtifactBulkEditPanel(Collection artifactIds, Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
        this.service = galaxy.getRegistryService();
        this.artifactIds = artifactIds;

        // main wrapper panel for this edit screen
        this.wrapperPanel = new VerticalPanel();

        // subpanels for each editable section
        this.lifecyclePanel = new FlowPanel();
        this.securityPanel = new FlowPanel();
        this.propertyPanel = new FlowPanel();

        // widgets
        this.lifecycleCB = new CheckBox();
        this.securityCB = new CheckBox();
        this.setPropertyCB = new CheckBox();
        this.delPropertyCB = new CheckBox();
        this.lifecycleLB = new ListBox();
        this.phaseLB = new ListBox();
        this.setPropertyLB = new ListBox();
        this.delPropertyLB = new ListBox();
        this.setPropertyTB = new TextBox();

        // main root panel
        menuPanel = new RegistryMenuPanel(galaxy);
        menuPanel.setMain(wrapperPanel);
        initWidget(menuPanel);
    }


    /**
     * tell the left side menu panel to draw itself
     *
     * @param params
     */
    public void onShow(List params) {
        menuPanel.clearErrorMessage();
        menuPanel.onShow();
        this.onShow();

    }


    /**
     * main init method for this screen
     */
    public void onShow() {

        wrapperPanel.clear();

        // by default they are all disabled
        lifecycleLB.setEnabled(false);
        phaseLB.setEnabled(false);
        delPropertyLB.setEnabled(false);
        setPropertyLB.setEnabled(false);
        setPropertyTB.setEnabled(false);

        Label label = new Label("Bulk Edit (" + artifactIds.size() + ")");
        label.setStyleName("title");
        wrapperPanel.add(label);
        wrapperPanel.setStyleName("bulkedit-panel");

        // add whatever property panels we are allowed bulk edit
        wrapperPanel.add(createLifecyclePanel());
        wrapperPanel.add(createSecurityPanel());
        wrapperPanel.add(createPropertyPanel());

        // save and cancel buttons
        save = new Button("Save");
        cancel = new Button("Cancel");
        save.addClickListener(this);
        cancel.addClickListener(this);
        wrapperPanel.add(asHorizontal(save, cancel));

        menuPanel.setMain(wrapperPanel);
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
            Window.alert("Save!");
            save();

        } else if (sender == cancel) {
            cancel();

        } else if (sender == lifecycleCB) {

            // toggle visibility both off and on
            lifecycleLB.setEnabled(checked);
            phaseLB.setEnabled(checked);

            // populate phases based on lifecycle value
            updatePhaseListBox(getLifecycleById(lifecycleLB.getValue(lifecycleLB.getSelectedIndex())));

        } else if (sender == setPropertyCB) {
            setPropertyLB.setEnabled(checked);
            setPropertyTB.setEnabled(checked);

        } else if (sender == delPropertyCB) {
            delPropertyLB.setEnabled(checked);
        }
    }


    /**
     * Too many anonymous inner classes will create a listener object overhead.
     * This will allow a single listener to distinguish between multiple event publishers.
     *
     * @param sender
     */
    public void onChange(Widget sender) {
        if (sender == lifecycleLB) {
            updatePhaseListBox(getLifecycleById(lifecycleLB.getValue(lifecycleLB.getSelectedIndex())));
        }
    }


    /**
     * The available phases are dependant on lifecycle
     *
     * @param w
     */
    private void updatePhaseListBox(WLifecycle w) {
        phaseLB.clear();
        for (Iterator iterator = w.getPhases().iterator(); iterator.hasNext();) {
            WPhase p = (WPhase) iterator.next();
            phaseLB.addItem(p.getName(), p.getId());
        }
    }


    /**
     * Configure property select boxes
     */
    private void updatePropertyListBox() {
        service.getPropertyDescriptors(new AbstractCallback(this) {
            public void onSuccess(Object result) {
                Collection props = (Collection) result;

                int i = 1;
                for (Iterator itr = props.iterator(); itr.hasNext();) {
                    final WPropertyDescriptor prop = (WPropertyDescriptor) itr.next();
                    setPropertyLB.addItem(prop.getName(), prop.getId());
                    delPropertyLB.addItem(prop.getName(), prop.getId());
                }
            }

        });

    }


    /**
     * Available Lifecycles
     */
    private void updateLifeCycleListBox() {
        lifecycleLB.clear();
        service.getLifecycles(new AbstractCallback(this) {
            public void onSuccess(Object arg0) {
                lifecycles = (Collection) arg0;
                for (Iterator iterator = lifecycles.iterator(); iterator.hasNext();) {
                    WLifecycle l = (WLifecycle) iterator.next();
                    lifecycleLB.addItem(l.getName(), l.getId());
                }
            }

        });


    }


    private SimplePanel createTitlePanel(String title) {
        SimplePanel s = new SimplePanel();
        Label l = new Label(title);
        l.setStyleName("bulkedit-section-title");
        s.add(l);
        s.setStyleName("bulkedit-section-header");
        return s;
    }


    private FlowPanel createLifecyclePanel() {
        FlexTable table = createItemTable();

        // init the avialable lifecycles
        updateLifeCycleListBox();

        lifecycleCB.addClickListener(this);
        lifecycleLB.addChangeListener(this);

        // lifecycle
        lifecyclePanel.add(createTitlePanel("Governance"));
        table.setWidget(0, 0, lifecycleCB);
        table.setText(0, 1, " Lifecycle: ");
        table.setWidget(0, 2, lifecycleLB);

        // phases
        table.setText(0, 3, "");
        table.setText(0, 4, " Phase: ");
        table.setWidget(0, 5, phaseLB);

        lifecyclePanel.add(table);
        return lifecyclePanel;
    }

    private FlowPanel createSecurityPanel() {
        FlexTable table = createItemTable();

        // get avaialable groups and permissions
        getPermissions();
        getGroups();

        securityCB.addClickListener(this);

        securityPanel.add(createTitlePanel("Security"));
        table.setWidget(0, 0, securityCB);
        securityPanel.add(table);

        return securityPanel;
    }


    private void getPermissions() {
        galaxy.getSecurityService().getPermissions(SecurityService.ARTIFACT_PERMISSIONS, new AbstractCallback(this) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
            }

        });
    }

    private void getGroups() {
        galaxy.getSecurityService().getGroupPermissions(new AbstractCallback(this) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
            }

        });
    }

    protected Widget createGrantWidget() {
        ListBox lb = new ListBox();
        lb.addItem("Revoked");
        lb.addItem("Inherited");
        lb.addItem("Granted");
        return lb;
    }


    private FlowPanel createPropertyPanel() {
        FlexTable table = createItemTable();

        setPropertyCB.addClickListener(this);
        delPropertyCB.addClickListener(this);

        propertyPanel.add(createTitlePanel("Set or Remove Property"));

        // init the available properties
        updatePropertyListBox();

        table.setWidget(0, 0, setPropertyCB);
        table.setText(0, 1, "Set: ");
        table.setWidget(0, 2, setPropertyLB);
        table.setText(0, 3, "Value: ");
        table.setWidget(0, 4, setPropertyTB);

        table.setWidget(1, 0, delPropertyCB);
        table.setText(1, 1, "Remove: ");
        table.setWidget(1, 2, delPropertyLB);

        propertyPanel.add(table);

        return propertyPanel;
    }


    // helper method to pop lifecycle out of collection by ID
    private WLifecycle getLifecycleById(String id) {
        for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
            WLifecycle l = (WLifecycle) itr.next();
            if (l.getId() != null && l.getId().equals(id)) {
                return l;
            }
        }
        return null;
    }


    // main save method for this class
    public void save() {
        if (lifecycleCB.isChecked()) {
            saveLifecycleAndPhase(lifecycleLB.getValue(lifecycleLB.getSelectedIndex()),
                                  phaseLB.getValue(phaseLB.getSelectedIndex()));
        }
        if (securityCB.isChecked()) {
            saveSecurity();
        }
        if (setPropertyCB.isChecked()) {
            saveProperty(setPropertyLB.getValue(setPropertyLB.getSelectedIndex()),
                         setPropertyTB.getText());
        }
        if (delPropertyCB.isChecked()) {
            deleteProperty(delPropertyLB.getValue(delPropertyLB.getSelectedIndex()));
        }

    }


    public void cancel() {
        History.back();
    }


    private void saveLifecycleAndPhase(String lifecycle, String phase) {

        service.transition(artifactIds, lifecycle, phase, new AbstractCallback(this) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
            }

        });
    }

    // TODO:
    private void saveSecurity() {

    }


    private void saveProperty(String name, String value) {

        service.setProperty(artifactIds, name, value, new AbstractCallback(this) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
            }

        });

    }


    private void deleteProperty(String name) {
        service.deleteProperty(artifactIds, name, new AbstractCallback(this) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
            }

        });

    }

    private FlexTable createItemTable() {
        FlexTable table = createTable();
        table.setStyleName("bulkedit-section-table");
        table.setCellSpacing(4);
        return table;
    }

}
