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
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WLifecycle;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class ArtifactBulkEditPanel extends AbstractErrorShowingComposite {

    private Collection artifacts;
    private SimplePanel lifecyclePanel;
    private SimplePanel phasePanel;
    private SimplePanel securityPanel;
    private FlowPanel panel;
    private final Galaxy galaxy;
    private RegistryMenuPanel menuPanel;
    private RegistryServiceAsync service;
    private CheckBox securityCB;
    private CheckBox lifecycleCB;
    private CheckBox phaseCB;
    private ListBox lifecyclesLB;
    private Button save;
    private Button cancel;


    public ArtifactBulkEditPanel(Collection artifacts, Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
        this.service = galaxy.getRegistryService();
        this.artifacts = artifacts;

        // main wrapper panel for this edit screen
        this.panel = new FlowPanel();
        // subpanels and checkboxes for each editable section
        this.lifecyclePanel = new SimplePanel();
        this.securityPanel = new SimplePanel();
        this.phasePanel = new SimplePanel();
        this.lifecycleCB = new CheckBox();
        this.securityCB = new CheckBox();
        this.phaseCB = new CheckBox();

        // main root panel
        menuPanel = new RegistryMenuPanel(galaxy);
        menuPanel.setMain(panel);
        initWidget(menuPanel);
    }


    public void onShow(List params) {
        // tell the left side menu panel to draw itself
        menuPanel.onShow();
        this.onShow();

    }


    // main init method for this screen
    public void onShow() {
        Label label = new Label("Bulk Edit Artifacts");
        label.setStyleName("title");
        panel.add(label);

        // add whatever property panels we are allowed bulk edit
        panel.add(createLifecyclePanel());
        panel.add(createPhasePanel());
        panel.add(createSecurityPanel());

        // create save and cancel buttons
        save = new Button("Save");
        save.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                saveProperties();
            }
        });
        cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
            }
        });
        panel.add(asHorizontal(save, cancel));
        menuPanel.setMain(panel);
    }


    private SimplePanel createLifecyclePanel() {
        FlexTable table = createColumnTable();

        lifecyclesLB = new ListBox();
        lifecyclesLB.setEnabled(false);

        service.getLifecycles(new AbstractCallback(this) {
            public void onSuccess(Object arg0) {
                Collection o = (Collection) arg0;
                for (Iterator iterator = o.iterator(); iterator.hasNext();) {
                    WLifecycle l = (WLifecycle) iterator.next();
                    lifecyclesLB.addItem(l.getName(), l.getId());
                }
            }

        });

        lifecycleCB.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                lifecyclesLB.setEnabled(((CheckBox) widget).isChecked());
            }
        });


        table.setWidget(0, 0, lifecycleCB);
        table.setText(0, 1, "Lifecycle:");
        table.setWidget(0, 2, lifecyclesLB);
        lifecyclePanel.add(table);
        return lifecyclePanel;
    }


    private void saveLifeCycle(String name, String value) {
        for (Iterator itr = artifacts.iterator(); itr.hasNext();) {
            ArtifactGroup ag = (ArtifactGroup) itr.next();
            service.setProperty(ag.getName(), name, value, new AbstractCallback(this) {

                public void onFailure(Throwable caught) {
                    super.onFailure(caught);
                }

                public void onSuccess(Object arg0) {
                }

            });

        }


    }

    private void savePhase() {

    }

    private void saveSecurity() {

    }


    private SimplePanel createPhasePanel() {
        FlexTable table = createColumnTable();

        // TODO: load the phases associated with the current lifcycle
        table.setWidget(0, 0, phaseCB);
        table.setText(0, 1, "Phase:");

        phasePanel.add(table);
        return phasePanel;
    }


    private SimplePanel createSecurityPanel() {
        FlexTable table = createColumnTable();

        table.setWidget(0, 0, securityCB);
        table.setText(0, 1, "Security:");
        securityPanel.add(table);

        return securityPanel;
    }


    // main save method for this class
    public void saveProperties() {
        if (lifecycleCB.isChecked()) {
            saveLifeCycle("LifeCycle", lifecyclesLB.getValue(lifecyclesLB.getSelectedIndex()));
        }
    }


    public void cancel() {
    }


}
