package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.TransitionResponse;
import org.mule.galaxy.web.rpc.WApprovalMessage;
import org.mule.galaxy.web.rpc.WGovernanceInfo;

public class GovernancePanel extends Composite {

    private RegistryPanel registryPanel;
    private RegistryServiceAsync registryService;
    private FlowPanel panel;
    private ExtendedArtifactInfo info;
    private FlowPanel messages;

    public GovernancePanel(RegistryPanel registryPanel,
                           ExtendedArtifactInfo info) {
        super();
        this.registryPanel = registryPanel;
        this.registryService = registryPanel.getRegistryService();
        this.info = info;
        
        panel = new FlowPanel();
        
        registryService.getGovernanceInfo(info.getId(), new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initializePanel((WGovernanceInfo) o);
            } 
            
        });
        initWidget(panel);
    }

    protected void initializePanel(WGovernanceInfo gov) {
        panel.add(createTitle("Lifecycle Management"));
        
        panel.add(createLifecycleTable(gov));
        
        messages = new FlowPanel();
        messages.setStyleName("policy-messages");
        panel.add(messages);
        
//        panel.add(createTitle("Policy Management"));
        
    }

    private InlineFlowPanel createTitle(String title) {
        InlineFlowPanel titlePanel = new InlineFlowPanel();
        titlePanel.setStyleName("rightlinked-title-panel");
        
        Label label = new Label(title);
        label.setStyleName("rightlinked-title");
        titlePanel.add(label);
        return titlePanel;
    }

    private FlexTable createLifecycleTable(WGovernanceInfo gov) {
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.getColumnFormatter().setStyleName(0, "artifactTableHeader");
        table.getColumnFormatter().setStyleName(1, "artifactTableEntry");
        
        table.setText(0, 0, "Lifecycle:");
        table.setText(0, 1, gov.getLifecycle());
        
        table.setText(1, 0, "Current Phase:");
        table.setText(1, 1, gov.getCurrentPhase());

        table.setText(2, 0, "Next Phases:");
        InlineFlowPanel nextPhasesPanel = new InlineFlowPanel();
        nextPhasesPanel.setStyleName("next-phases-panel");
        
        final ListBox nextPhasesList = new ListBox();
        for (Iterator itr = gov.getNextPhases().iterator(); itr.hasNext();){
            nextPhasesList.addItem((String) itr.next());
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
        
        return table;
    }

    protected void transition(final ListBox nextPhasesList, 
                              final Button transitionButton) {
        transitionButton.setEnabled(false);
        transitionButton.setText("Applying transition...");
        messages.clear();
        
        int idx = nextPhasesList.getSelectedIndex();
        
        registryService.transition(info.getId(), nextPhasesList.getValue(idx), new AbstractCallback(registryPanel) {

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
            registryService.getGovernanceInfo(info.getId(), new AbstractCallback(registryPanel) {
    
                public void onSuccess(Object o) {
                    panel.clear();
                    
                    initializePanel((WGovernanceInfo) o);
                } 
                
            });
        }
    }

}
