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
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.TransitionResponse;
import org.mule.galaxy.web.rpc.WApprovalMessage;
import org.mule.galaxy.web.rpc.WGovernanceInfo;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

public class GovernancePanel extends AbstractComposite {

    private RegistryServiceAsync registryService;
    private FlowPanel panel;
    private FlowPanel messages;
    private ListBox lifecyclesLB;
    private Collection lifecycles;
    private WGovernanceInfo governanceInfo;
    private final ArtifactVersionInfo version;
    private final Galaxy galaxy;
    private final ErrorPanel errorPanel;

    public GovernancePanel(Galaxy galaxy,
                           ErrorPanel errorPanel,
                           ArtifactVersionInfo version) {
        super();
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.version = version;
        this.registryService = galaxy.getRegistryService();
        
        panel = new FlowPanel();
        
        registryService.getGovernanceInfo(version.getId(), new AbstractCallback(errorPanel) {

            public void onSuccess(Object o) {
                initializePanel((WGovernanceInfo) o);
            } 
            
        });
        
        initWidget(panel);
        
    }
    
    protected void initializePanel(WGovernanceInfo gov) {
        this.governanceInfo = gov;
        panel.add(createTitle("Lifecycle Management"));
        
        panel.add(createLifecycleTable());
        
        messages = new FlowPanel();
        messages.setStyleName("policy-messages");
        panel.add(messages);
        
//        panel.add(createTitle("Policy Management"));
        
    }

    private FlexTable createLifecycleTable() {
        FlexTable table = createColumnTable();
        
        table.setText(0, 0, "Lifecycle:");
        showLifecycle(table, governanceInfo.getLifecycle());
        
        table.setText(1, 0, "Current Phase:");
        table.setText(1, 1, governanceInfo.getCurrentPhase());

        table.setText(2, 0, "Next Phases:");
        FlowPanel nextPhasesPanel = new FlowPanel();
        nextPhasesPanel.setStyleName("next-phases-panel");
        
        final ListBox nextPhasesList = new ListBox();
        for (Iterator itr = governanceInfo.getNextPhases().iterator(); itr.hasNext();){
            WPhase p = (WPhase) itr.next();
            nextPhasesList.addItem(p.getName(), p.getId());
        }
        nextPhasesPanel.add(nextPhasesList);
        
        final Button transition = new Button("Transition");
        transition.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                transition(nextPhasesList, transition);
            }
        });
        nextPhasesPanel.add(transition);
        
        table.setWidget(2, 1, nextPhasesPanel);
        styleHeaderColumn(table);
        return table;
    }

    private void showLifecycle(final FlexTable table, String lifecycle) {
        InlineFlowPanel lifecycleEdit = new InlineFlowPanel();
        lifecycleEdit.add(new Label(lifecycle + " "));
        
        Hyperlink editHL = new Hyperlink("Edit", "edit-artifact-lifecycle");
        editHL.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                editLifecycle(table);
            }
        });
        lifecycleEdit.add(editHL);
        
        table.setWidget(0, 1, lifecycleEdit);
    }

    protected void editLifecycle(final FlexTable table) {
        lifecyclesLB = new ListBox();
        lifecyclesLB.addItem("Loading...");
        lifecyclesLB.setEnabled(false);
        
        table.setWidget(0, 1, lifecyclesLB);
        registryService.getLifecycles(new AbstractCallback(errorPanel) {

            public void onSuccess(Object o) {
                initLifecycles(table, (Collection) o);
            }
        });
    }

    protected void initLifecycles(final FlexTable table, Collection o) {
        this.lifecycles = o;
        lifecyclesLB.clear();
        for (Iterator iterator = o.iterator(); iterator.hasNext();) {
            WLifecycle l = (WLifecycle)iterator.next();
            
            lifecyclesLB.addItem(l.getName(), l.getId());
        }
        lifecyclesLB.setEnabled(true);
        
        Button save = new Button("Save");
        save.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                saveLifecycle(table);
            }
        });
        
        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                showLifecycle(table, governanceInfo.getLifecycle());
            }
        });
        
        FlowPanel lPanel = new FlowPanel();
        lPanel.add(lifecyclesLB);
        lPanel.add(save);
        lPanel.add(cancel);
        
        table.setWidget(0, 1, lPanel);
    }

    protected void saveLifecycle(final FlexTable table) {
        final WLifecycle lifecycle = getSelectedLifecycle();
        final WPhase p = lifecycle.getInitialPhase();
        
        if (lifecycle.getName().equals(governanceInfo.getLifecycle())) {
            showLifecycle(table, lifecycle.getName());
            return;
        }
        galaxy.getRegistryService().transition(version.getId(), p.getId(), new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object o) {
                showLifecycle(table, lifecycle.getName());
                
                displayTransitionResponse((TransitionResponse) o);
            }
            
        });
    }

    private WLifecycle getSelectedLifecycle() {
        int idx = lifecyclesLB.getSelectedIndex();
        String id = lifecyclesLB.getValue(idx);
        
        for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
            WLifecycle l = (WLifecycle)itr.next();
            
            if (l.getId().equals(id)) {
                return l;
            }
        }
        return null;
    }

    protected void transition(final ListBox nextPhasesList, 
                              final Button transitionButton) {
        transitionButton.setEnabled(false);
        transitionButton.setText("Applying transition...");
        messages.clear();
        
        int idx = nextPhasesList.getSelectedIndex();
        
        registryService.transition(version.getId(), nextPhasesList.getValue(idx), new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                transitionButton.setEnabled(true);
                transitionButton.setText("Transition");
                
                super.onFailure(caught);
            }

            public void onSuccess(Object o) {
                displayTransitionResponse((TransitionResponse) o);
            }
            
        });
     }

    protected void displayTransitionResponse(TransitionResponse o) {
        if (!o.isSuccess()) {
            for (Iterator iterator = o.getMessages().iterator(); iterator.hasNext();) {
                WApprovalMessage app = (WApprovalMessage)iterator.next();
                
                String msg = app.getMessage();
                
                if (app.isWarning()){
                    msg = "WARNING: " + msg;
                }
                
                messages.add(new Label(msg));
            }
        } else {
            registryService.getGovernanceInfo(version.getId(), new AbstractCallback(errorPanel) {
    
                public void onSuccess(Object o) {
                    panel.clear();
                    
                    initializePanel((WGovernanceInfo) o);
                } 
                
            });
        }
    }
}
