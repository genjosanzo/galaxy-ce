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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextBox;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class ArtifactBulkEditPanel extends AbstractErrorShowingComposite
        implements ClickListener, ChangeListener {

    private Collection artifactIds;
    private Collection lifecycles;

    private SimplePanel lifecyclePanel;
    private SimplePanel securityPanel;
    private SimplePanel propertyPanel;

    private FlowPanel wrapperPanel;
    private final Galaxy galaxy;
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
        this.wrapperPanel = new FlowPanel();

        // subpanels for each editable section
        this.lifecyclePanel = new SimplePanel();
        this.securityPanel = new SimplePanel();
        this.propertyPanel = new SimplePanel();

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

        this.save = new Button("Save");
        this.cancel = new Button("Cancel");

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
        menuPanel.onShow();
        this.onShow();

    }

    /**
     * main init method for this screen
     */
    public void onShow() {

        lifecycleLB.setEnabled(false);
        phaseLB.setEnabled(false);
        delPropertyLB.setEnabled(false);
        setPropertyLB.setEnabled(false);

        Label label = new Label("Bulk Edit (" + artifactIds.size() + ")");
        label.setStyleName("title");
        wrapperPanel.add(label);

        // add whatever property panels we are allowed bulk edit
        wrapperPanel.add(createLifecyclePanel());
        wrapperPanel.add(createSecurityPanel());
        wrapperPanel.add(createPropertyPanel());

        // save and cancel buttons
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
        if(sender instanceof CheckBox) {
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


    private SimplePanel createLifecyclePanel() {
        FlexTable table = createColumnTable();

        // init the avialable lifecycles
        updateLifeCycleListBox();

        lifecycleCB.addClickListener(this);
        lifecycleLB.addChangeListener(this);

        // lifecycle
        table.setWidget(0, 0, lifecycleCB);
        table.setText(0, 1, "Lifecycle:");
        table.setWidget(0, 2, lifecycleLB);

        // phases
        table.setText(1, 0, "");
        table.setText(1, 1, "Phase:");
        table.setWidget(1, 2, phaseLB);

        lifecyclePanel.add(table);
        return lifecyclePanel;
    }


    private SimplePanel createSecurityPanel() {
        FlexTable table = createColumnTable();

        securityCB.addClickListener(this);

        table.setWidget(0, 0, securityCB);
        table.setText(0, 1, "Security:");
        securityPanel.add(table);

        return securityPanel;
    }


    private SimplePanel createPropertyPanel() {
        FlexTable table = createColumnTable();

        setPropertyCB.addClickListener(this);
        delPropertyCB.addClickListener(this);

        // init the available properties
        updatePropertyListBox();

        table.setWidget(0, 0, setPropertyCB);
        table.setText(0, 1, "Set Property:");
        table.setWidget(0, 2, setPropertyLB);
        table.setText(0, 3, "Value:");
        table.setWidget(0, 4, setPropertyTB);

        table.setWidget(1, 0, delPropertyCB);
        table.setText(1, 1, "Delete Property:");
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
        }
        if (setPropertyCB.isChecked()) {
        }
        if (delPropertyCB.isChecked()) {
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


    private void saveSecurity() {

    }


    private void saveProperty() {

    }


    private void deleteProperty() {

    }


}
