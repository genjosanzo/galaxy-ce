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

package org.mule.galaxy.repository.client.util;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WPolicy;
import org.mule.galaxy.web.client.ui.panel.AbstractShowable;
import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PolicySelectionPanel extends AbstractShowable {

    private ListBox unselectedPolicies;
    private ListBox selectedPolicies;
    private RegistryServiceAsync registryService;
    private final ErrorPanel errorPanel;
    protected Collection policies;
    private SimplePanel descriptionPanel;
    private FlowPanel panel;
    private Collection<String> selectedPolicyIds;
    private boolean loaded;
    private Button left;
    private Button right;

    public PolicySelectionPanel(ErrorPanel errorPanel, RegistryServiceAsync svc) {
        super();
        this.errorPanel = errorPanel;
        this.registryService = svc;
        panel = new FlowPanel();
        
        panel.add(createTitle("Applied Policies"));
        
        FlexTable table = createTable();
        
        unselectedPolicies = new ListBox();
        unselectedPolicies.setMultipleSelect(true);
        unselectedPolicies.setVisibleItemCount(10);
        
        selectedPolicies = new ListBox();
        selectedPolicies.setMultipleSelect(true);
        selectedPolicies.setVisibleItemCount(10);
        
        descriptionPanel = new SimplePanel();
        
        ChangeListener selectionListener = new ChangeListener() {
            public void onChange(Widget w) {
                WPolicy p = findArtifactPolicy((ListBox) w);
                if (p != null) {
                    descriptionPanel.clear();
                    descriptionPanel.add(new Label("Description: " + p.getDescription()));
                }
            }
        };
        
        unselectedPolicies.addChangeListener(selectionListener);
        selectedPolicies.addChangeListener(selectionListener);
        
        table.setWidget(0, 0, unselectedPolicies);       
        table.setWidget(0, 2, selectedPolicies);

        VerticalPanel mid = new VerticalPanel();

        right = new Button(">");
        right.setStyleName("smallButton");
        right.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                move(unselectedPolicies, selectedPolicies);
            }
        });
        mid.add(right);
        
        left = new Button("&lt;");
        left.setStyleName("smallButton");
        left.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                move(selectedPolicies, unselectedPolicies);
            }
        });
        mid.add(left);

        table.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
        
        table.setWidget(0, 1, mid);

        panel.add(table);
        
        panel.add(descriptionPanel);
        
        initWidget(panel);
    }

    protected void move(ListBox from, ListBox to) {
        int idx;
        while ((idx = from.getSelectedIndex()) != -1)
        {
            move(from, to, idx);
        }
    }

    private void move(ListBox from, ListBox to, int idx) {
        String value = from.getValue(idx);
        String item = from.getItemText(idx);
        
        from.removeItem(idx);
        
        to.addItem(item, value);
        
        if (from == selectedPolicies) {
            selectedPolicyIds.remove(value);
        } else {
            selectedPolicyIds.add(value);
        }
    }

    protected WPolicy findArtifactPolicy(ListBox w) {
        for (Iterator itr = policies.iterator(); itr.hasNext();) {
            WPolicy p = (WPolicy)itr.next();
            
            int idx = w.getSelectedIndex();
            if (idx == -1) {
                return null;
            }
            
            if (p.getId().equals(w.getValue(idx))) {
                return p;
            }
        }
        return null;
    }

    private void loadPolicies() {
        unselectedPolicies.clear();
        selectedPolicies.clear();
        
        registryService.getPolicies(new AbstractCallback(errorPanel) {

            public void onCallSuccess(Object o) {
                policies = (Collection) o;
                
                for (Iterator itr = policies.iterator(); itr.hasNext();) {
                    WPolicy p = (WPolicy)itr.next();
                    
                    if (selectedPolicyIds.contains(p.getId())) {
                        selectedPolicies.addItem(p.getName(), p.getId());
                    } else {
                        unselectedPolicies.addItem(p.getName(), p.getId());
                    }
                }
            }
        });                         
    }

    public void selectAndShow(Collection<String> ids) {
        this.selectedPolicyIds = ids;
        this.loaded = true;
        
        loadPolicies();
    }
    
    public boolean isLoaded() {
        return loaded;
    }

    public void setEnabled(boolean enabled) {
        unselectedPolicies.setEnabled(enabled);
        selectedPolicies.setEnabled(enabled);
        right.setEnabled(enabled);
        left.setEnabled(enabled);
    }

    public Collection<String> getSelectedPolicyIds() {
        return selectedPolicyIds;
    }
}
