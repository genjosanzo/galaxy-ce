package org.mule.galaxy.web.client.artifact;

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

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.TransitionResponse;
import org.mule.galaxy.web.rpc.WApprovalMessage;
import org.mule.galaxy.web.rpc.WGovernanceInfo;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

public class GovernancePanel extends AbstractComposite {

    private RegistryPanel registryPanel;
    private RegistryServiceAsync registryService;
    private FlowPanel panel;
    private FlowPanel messages;
    private ListBox lifecyclesLB;
    private Collection lifecycles;
    private WGovernanceInfo governanceInfo;
    private final ArtifactVersionInfo version;

    public GovernancePanel(RegistryPanel registryPanel,
                           ArtifactVersionInfo version) {
        super();
        this.registryPanel = registryPanel;
        this.version = version;
        this.registryService = registryPanel.getRegistryService();
        
        panel = new FlowPanel();
        
        registryService.getGovernanceInfo(version.getId(), new AbstractCallback(registryPanel) {

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
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        
        initLifecycle(table, governanceInfo);
        
        table.setText(1, 0, "Current Phase:");
        table.setText(1, 1, governanceInfo.getCurrentPhase());

        table.setText(2, 0, "Next Phases:");
        InlineFlowPanel nextPhasesPanel = new InlineFlowPanel();
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

    private void initLifecycle(final FlexTable table, WGovernanceInfo gov) {
        InlineFlowPanel lifecycleEdit = new InlineFlowPanel();
        lifecycleEdit.add(new Label("Lifecycle: ["));
        Hyperlink editHL = new Hyperlink("Edit", "edit-artifact-lifecycle");
        editHL.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                editLifecycle(table);
            }
        });
        lifecycleEdit.add(editHL);
        lifecycleEdit.add(new Label("]"));
        
        table.setWidget(0, 0, lifecycleEdit);
        table.setText(0, 1, gov.getLifecycle());
    }

    protected void editLifecycle(final FlexTable table) {
        lifecyclesLB = new ListBox();
        lifecyclesLB.addItem("Loading...");
        lifecyclesLB.setEnabled(false);
        
        table.setWidget(0, 1, lifecyclesLB);
        registryService.getLifecycles(new AbstractCallback(registryPanel) {

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
                table.setText(0, 1, governanceInfo.getLifecycle());
            }
        });
        
        InlineFlowPanel lPanel = new InlineFlowPanel();
        lPanel.add(lifecyclesLB);
        lPanel.add(save);
        
        table.setWidget(0, 1, lPanel);
    }

    protected void saveLifecycle(final FlexTable table) {
        final WLifecycle lifecycle = getSelectedLifecycle();
        final WPhase p = lifecycle.getInitialPhase();
        
        if (lifecycle.getName().equals(governanceInfo.getLifecycle())) {
            table.setText(0, 1, lifecycle.getName());
            return;
        }
        registryPanel.getRegistryService().transition(version.getId(), p.getId(), 
                                                      new AbstractCallback(registryPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object o) {
                table.setText(0, 1, lifecycle.getName());
                
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
        
        registryService.transition(version.getId(), nextPhasesList.getValue(idx), new AbstractCallback(registryPanel) {

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
            registryService.getGovernanceInfo(version.getId(), new AbstractCallback(registryPanel) {
    
                public void onSuccess(Object o) {
                    panel.clear();
                    
                    initializePanel((WGovernanceInfo) o);
                } 
                
            });
        }
    }
}
