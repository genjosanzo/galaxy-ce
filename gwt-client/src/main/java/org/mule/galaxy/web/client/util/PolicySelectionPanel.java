package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WArtifactPolicy;

public class PolicySelectionPanel extends AbstractComposite{

    private ListBox unselectedPolicies;
    private ListBox selectedPolicies;
    private RegistryServiceAsync registryService;
    private final AbstractMenuPanel menuPanel;
    protected Collection policies;
    private SimplePanel descriptionPanel;
    private FlowPanel panel;
    private Collection selectedPolicyIds;
    private boolean loaded;
    private Button left;
    private Button right;

    public PolicySelectionPanel(AbstractMenuPanel menuPanel, RegistryServiceAsync svc) {
        super();
        this.menuPanel = menuPanel;
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
                WArtifactPolicy p = findArtifactPolicy((ListBox) w);
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
        
        FlowPanel mid = new FlowPanel();
        left = new Button("&lt;");
        left.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                move(selectedPolicies, unselectedPolicies);
            }
        });
        mid.add(left);
        right = new Button("&gt;");
        right.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                move(unselectedPolicies, selectedPolicies);
            }
        });
        mid.add(right);
        
        table.setWidget(0, 1, mid);
        
        panel.add(table);
        
        panel.add(descriptionPanel);
        
        initWidget(panel);
    }

    protected void move(ListBox from, ListBox to) {
        int idx = from.getSelectedIndex();
        
        if (idx == -1) return;
        
        move(from, to, idx);
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

    protected WArtifactPolicy findArtifactPolicy(ListBox w) {
        for (Iterator itr = policies.iterator(); itr.hasNext();) {
            WArtifactPolicy p = (WArtifactPolicy)itr.next();
            
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
        
        registryService.getPolicies(new AbstractCallback(menuPanel) {

            public void onSuccess(Object o) {
                policies = (Collection) o;
                
                for (Iterator itr = policies.iterator(); itr.hasNext();) {
                    WArtifactPolicy p = (WArtifactPolicy)itr.next();
                    
                    if (selectedPolicyIds.contains(p.getId())) {
                        selectedPolicies.addItem(p.getName(), p.getId());
                    } else {
                        unselectedPolicies.addItem(p.getName(), p.getId());
                    }
                }
            }
        });                         
    }

    public void selectAndShow(Collection ids) {
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

    public Collection getSelectedPolicyIds() {
        return selectedPolicyIds;
    }
}
