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

package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.item.PolicyResultsPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.LifecycleSelectionPanel;
import org.mule.galaxy.web.client.util.PolicySelectionPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WPolicyException;

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

public class PolicyPanel extends AbstractComposite {

    private InlineFlowPanel panel;
    private RegistryServiceAsync svc;
    private LifecycleSelectionPanel lsPanel;
    private SimplePanel psPanelContainer;
    private final ErrorPanel menuPanel;

    private Map<String, Map<String, PolicySelectionPanel>> lifecycle2Phase2Panel = new HashMap<String, Map<String, PolicySelectionPanel>>();
    private PolicySelectionPanel currentPsPanel;
    private Button saveButton;
    private boolean finishedSave;
    private int saveCount;
    private String workspaceId;
    private final Galaxy galaxy;

    public PolicyPanel(ErrorPanel adminPanel, Galaxy galaxy) {
        this(adminPanel, galaxy, null);
    }

    public PolicyPanel(ErrorPanel adminPanel, Galaxy galaxy, String workspaceId) {
        super();
        this.menuPanel = adminPanel;
        this.galaxy = galaxy;
        this.workspaceId = workspaceId;
        this.svc = galaxy.getRegistryService();

        panel = new InlineFlowPanel();

        initWidget(panel);

    }

    @Override
    public void doShow() {
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

                if (caught instanceof WPolicyException) {
                    handlePolicyFailure(galaxy, (WPolicyException) caught);
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
                Collection<String> active = ((PolicySelectionPanel) phaseEntry.getValue()).getSelectedPolicyIds();

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

    public static void handlePolicyFailure(Galaxy galaxy, WPolicyException caught) {
        PageInfo page =
            galaxy.createPageInfo("policy-failure-" + caught.hashCode(),
                                  new PolicyResultsPanel(galaxy, caught.getPolicyFailures()),
                                  0);

        History.newItem(page.getName());
    }

    protected void loadPolicies() {
        final String lifecycle = lsPanel.getSelectedLifecycle();
        final String phase = lsPanel.getSelectedPhase();

        currentPsPanel = getPanel(lifecycle, phase);

        psPanelContainer.clear();
        psPanelContainer.add(currentPsPanel);

        AbstractCallback callback = new AbstractCallback(menuPanel) {
            @SuppressWarnings("unchecked")
            public void onSuccess(Object o) {
               currentPsPanel.selectAndShow((Collection<String>) o);
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
        Map<String, PolicySelectionPanel> phase2Panel = lifecycle2Phase2Panel.get(lifecycle);
        if (phase2Panel == null) {
            phase2Panel = new HashMap<String, PolicySelectionPanel>();
            lifecycle2Phase2Panel.put(lifecycle, phase2Panel);
        }

        PolicySelectionPanel psPanel = phase2Panel.get(phase);
        if (psPanel == null) {
            psPanel = new PolicySelectionPanel(menuPanel, svc);
            phase2Panel.put(phase, psPanel);
        }
        return psPanel;
    }
}
