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

package org.mule.galaxy.repository.client.item;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.WApprovalMessage;
import org.mule.galaxy.web.client.ui.panel.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;

public class PolicyResultsPanel extends AbstractErrorShowingComposite {

    private final Map<ItemInfo, Collection<WApprovalMessage>> policyFailures;
    private FlowPanel panel;

    public PolicyResultsPanel(Galaxy galaxy, Map<ItemInfo, Collection<WApprovalMessage>> policyFailures) {
        super();
        this.policyFailures = policyFailures;
        FlowPanel base = getMainPanel();
        
        base.setStyleName("policy-failure-panel-base");

        panel = new FlowPanel();
        panel.setStyleName("policy-failure-panel");
        base.add(panel);
        
        initWidget(base);
    }
    
    @Override
    public void doShowPage() {
        panel.clear();
        
        panel.add(createTitle("Policy Failures"));
        panel.add(new Label("This change was not allowed as not all policies were met."));
        
        for (Iterator itr = policyFailures.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry) itr.next();
            ItemInfo info = (ItemInfo) e.getKey();
            List approvals = (List) e.getValue();
            
            FlowPanel artifactPanel = new FlowPanel();
            artifactPanel.setStyleName("policy-result-artifact-panel");
            
            addArtifact(artifactPanel, info, approvals);
            panel.add(artifactPanel);
        }
    }
    
    public void addArtifact(FlowPanel artifactPanel, ItemInfo info, List approvals) {
        FlowPanel warningPanel = new FlowPanel();
        FlowPanel failurePanel = new FlowPanel();

        Hyperlink hl = new Hyperlink(info.getValue(0), "artifact/" + info.getId());
        hl.setStyleName("policy-result-artifact-link");
        artifactPanel.add(hl); 
        
        for (Iterator itr = approvals.iterator(); itr.hasNext();) {
            WApprovalMessage approval = (WApprovalMessage)itr.next();
            
            if (approval.isWarning()) {
                Label warningLabel = new Label(approval.getMessage());
                warningLabel.setStyleName("policy-result-warning-label");
                warningPanel.add(warningLabel);
            } else {
                Label failureLabel = new Label(approval.getMessage());
                failureLabel.setStyleName("policy-result-failure-label");
                failurePanel.add(failureLabel);
            }
        }
            
        if (warningPanel.getWidgetCount() > 0) {
            Label label = new Label("Warnings");
            label.setStyleName("policy-result-section-header");
            artifactPanel.add(label); 
            artifactPanel.add(warningPanel);
        }

        if (failurePanel.getWidgetCount() > 0) {
            Label label = new Label("Failures");
            label.setStyleName("policy-result-section-header");
            artifactPanel.add(label); 
            artifactPanel.add(failurePanel);
        }
    }

}
