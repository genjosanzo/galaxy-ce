package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

public class LifecycleSelectionPanel extends AbstractComposite {

    private ListBox lifecyclesLB;
    private ListBox phasesLB;
    private Collection lifecycles;
    
    public LifecycleSelectionPanel(AbstractMenuPanel menuPanel, RegistryServiceAsync svc) {
        super();
        
        FlowPanel panel = new FlowPanel();
        panel.add(createTitle("LifecycleImpl"));
        lifecyclesLB = new ListBox();
        lifecyclesLB.setVisibleItemCount(4);
        
        phasesLB = new ListBox();
        phasesLB.setVisibleItemCount(10);
        
        svc.getLifecycles(new AbstractCallback(menuPanel) {
            public void onSuccess(Object o) {
                initLifecycles((Collection)o);
            }
        });
        
        lifecyclesLB.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                selectLifecycle();
            }
            
        });

        Label label = new Label("Phases:");
        label.setStyleName("lifecycle-phases-label");
        
        panel.add(lifecyclesLB);
        panel.add(label);
        panel.add(phasesLB);
        
        initWidget(panel);
    }

    protected void selectLifecycle() {
        int idx = lifecyclesLB.getSelectedIndex();
        if (idx == -1) {
            Window.alert("No lifecycle selected");
            return;
        }
        
        String name = lifecyclesLB.getItemText(idx);
        
        WLifecycle l = getLifecycle(name);
        
        phasesLB.clear();
        
        phasesLB.addItem("All Phases", "_all");
        phasesLB.addItem("--", "_none");
        
        for (Iterator itr = l.getPhases().iterator(); itr.hasNext();) {
            WPhase phase = (WPhase)itr.next();
            
            phasesLB.addItem(phase.getName());
        }
    }

    private WLifecycle getLifecycle(String name) {
        for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
            WLifecycle l = (WLifecycle)itr.next();

            if (l.getName().equals(name)) {
                return l;
            }
        }
        return null;
    }

    protected void initLifecycles(Collection o) {
        this.lifecycles = o;
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            WLifecycle l = (WLifecycle)itr.next();
            
            lifecyclesLB.addItem(l.getName());
        }
        
        lifecyclesLB.setSelectedIndex(0);
        selectLifecycle();
    }

    public String getSelectedLifecycle() {
        int idx = lifecyclesLB.getSelectedIndex();
        if (idx == -1) {
            return null;
        }
        
        return lifecyclesLB.getValue(idx);
    }

    public String getSelectedPhase() {
        int idx = phasesLB.getSelectedIndex();
        if (idx == -1) {
            return null;
        }
        
        return phasesLB.getValue(idx);
    }

    public void addPhaseChangeListener(ChangeListener changeListener) {
        phasesLB.addChangeListener(changeListener);
    }

    public void setEnabled(boolean enabled) {
        phasesLB.setEnabled(enabled);
        lifecyclesLB.setEnabled(enabled);
    }

}
