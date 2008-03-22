package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.client.MenuPanelPageInfo;
import org.mule.galaxy.web.client.artifact.ArtifactCollectionPolicyResultsPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.LifecycleSelectionPanel;
import org.mule.galaxy.web.client.util.PolicySelectionPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ApplyPolicyException;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;

public class PolicyPanel extends AbstractComposite {

    private InlineFlowPanel panel;
    private RegistryServiceAsync svc;
    private LifecycleSelectionPanel lsPanel;
    private SimplePanel psPanelContainer;
    private final AbstractMenuPanel menuPanel;

    private Map lifecycle2Phase2Panel = new HashMap();
    private PolicySelectionPanel currentPsPanel;
    private Button saveButton;
    private boolean finishedSave;
    private int saveCount;
    private String workspaceId;
    
    public PolicyPanel(AbstractMenuPanel adminPanel, RegistryServiceAsync svc) {
        this(adminPanel, svc, null);
    }
    
    public PolicyPanel(AbstractMenuPanel adminPanel, RegistryServiceAsync svc, String workspaceId) {
        super();
        this.menuPanel = adminPanel;
        this.workspaceId = workspaceId;
        this.svc = svc;

        panel = new InlineFlowPanel();

        initWidget(panel);
        
    }

    public void onShow() {
        panel.clear();
        lsPanel = new LifecycleSelectionPanel(menuPanel, svc);
        
        psPanelContainer = new SimplePanel();
        
        FlexTable table = createTable();
        table.setCellPadding(5);
        table.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_TOP);
        
        table.setWidget(0, 0, lsPanel);
        table.setWidget(0, 1, psPanelContainer);
        lsPanel.addPhaseChangeListener(new ChangeListener() {

            public void onChange(Widget arg0) {
                loadPolicies();
            }
            
        });
        
        panel.add(table);
        
        saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                save();
            }
            
        });
        table.setWidget(1, 0, saveButton);
        table.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
        
    }

    protected void save() {
        currentPsPanel.setEnabled(false);
        lsPanel.setEnabled(false);
        
        saveButton.setText("Saving...");
        saveButton.setEnabled(false);
        
        AbstractCallback callback = new AbstractCallback(menuPanel) {

            public void onFailure(Throwable caught) {
                reenable();
                
                if (caught instanceof ApplyPolicyException) {
                    handlePolicyFailure((ApplyPolicyException) caught);
                } else {
                    super.onFailure(caught);
                }
            }

            private void reenable() {
                currentPsPanel.setEnabled(true);
                lsPanel.setEnabled(true);
                
                saveButton.setText("Save");
                saveButton.setEnabled(true);
            }

            public void onSuccess(Object arg0) {
                saveCount--;
                
                if (saveCount == 0 && finishedSave) {
                    menuPanel.setMessage("Policies were saved.");
                    reenable();
                    finishedSave = false;
                }
            }
            
        };
        
        for (Iterator itr = lifecycle2Phase2Panel.entrySet().iterator(); itr.hasNext();) {
            Map.Entry lifecycleEntry = (Map.Entry)itr.next();
            
            String lifecycle = (String) lifecycleEntry.getKey();
            Map phase2Panel = (Map) lifecycleEntry.getValue();
            
            for (Iterator pitr = phase2Panel.entrySet().iterator(); pitr.hasNext();) {
                Map.Entry phaseEntry = (Map.Entry)pitr.next();
                
                String phase = (String) phaseEntry.getKey();
                Collection active = ((PolicySelectionPanel) phaseEntry.getValue()).getSelectedPolicyIds();
                
                if ("_all".equals(phase)) {
                    svc.setActivePolicies(workspaceId, lifecycle, null, active, callback);
                } else {
                    svc.setActivePolicies(workspaceId, lifecycle, phase, active, callback);
                }
                
                saveCount++;
            }
        }
        
        finishedSave = true;

    }

    protected void handlePolicyFailure(final ApplyPolicyException caught) {
        MenuPanelPageInfo page = new MenuPanelPageInfo("policy-failure-" + caught.hashCode(), menuPanel) {

            public AbstractComposite createInstance() {
                return new ArtifactCollectionPolicyResultsPanel(caught.getPolicyFailures());
            }
            
        };
        menuPanel.addPage(page);
        History.newItem(page.getName());
    }

    protected void loadPolicies() {
        final String lifecycle = lsPanel.getSelectedLifecycle();
        final String phase = lsPanel.getSelectedPhase();
        
        currentPsPanel = getPanel(lifecycle, phase);
        
        psPanelContainer.clear();
        psPanelContainer.add(currentPsPanel);
        
        AbstractCallback callback = new AbstractCallback(menuPanel) {
            public void onSuccess(Object o) {
               currentPsPanel.selectAndShow((Collection) o);
            }

        };
        
        if (!currentPsPanel.isLoaded()) {
            if ("_all".equals(phase)) {
                svc.getActivePoliciesForLifecycle(lifecycle, workspaceId, callback);
            } else if (!"_none".equals(phase)) {
                svc.getActivePoliciesForPhase(lifecycle, phase, workspaceId, callback);
            } else {
                psPanelContainer.clear();
            }
        }
    }

    private PolicySelectionPanel getPanel(final String lifecycle, final String phase) {
        Map phase2Panel = (Map) lifecycle2Phase2Panel.get(lifecycle);
        if (phase2Panel == null) {
            phase2Panel = new HashMap();
            lifecycle2Phase2Panel.put(lifecycle, phase2Panel);
        }
        
        PolicySelectionPanel psPanel = (PolicySelectionPanel) phase2Panel.get(phase);
        if (psPanel == null) {
            psPanel = new PolicySelectionPanel(menuPanel, svc);
            phase2Panel.put(phase, psPanel);
        }
        return psPanel;
    }
}
