package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

public class LifecyclePropertyPanel extends AbstractEditPropertyPanel {

    private FlexTable lifecycleTable;
    private Label valueLabel;
    private WLifecycle lifecycle;
    private WPhase phase;
    protected Collection lifecycles;
    private ListBox lifecyclesLB;
    private ListBox phaseLB;
    
    protected Widget createEditForm() {
        lifecycleTable = createColumnTable();
        
        return lifecycleTable;
    }

    protected Object getValueToSave() {
        ArrayList<String> values = new ArrayList<String>();
        values.add(getSelectedLifecycle().getId());
        values.add(getSelectedPhase().getId());
        return values;
    }

    protected Widget createViewWidget() {
        valueLabel = new Label();
        return valueLabel;
    }

    public void initialize() {
        super.initialize();
        
        valueLabel.setText("Loading...");
        
        final List<String> ids = getProperty().getListValue();
        if (ids != null) {
            galaxy.getRegistryService().getLifecycle(ids.get(0), new AbstractCallback(errorPanel) {
    
                public void onSuccess(Object o) {
                    lifecycle = (WLifecycle) o;
                    phase = lifecycle.getPhaseById(ids.get(1));
                    
                    updateLabel();
                }
            });
        }
    }

    public void showEdit() {
        if (lifecycles == null) {
            galaxy.getRegistryService().getLifecycles(new AbstractCallback(errorPanel) {
                public void onSuccess(Object o) {
                    lifecycles = (Collection) o;
                    doShowLifecycles();
                }
            });
        } else {
            doShowLifecycles();
        }
        super.showEdit();
    }
    
    private void doShowLifecycles() {
        lifecycleTable.setText(0, 0, "Lifecycle:");
        lifecycleTable.setText(1, 0, "Phase:");

        String lid = null;
        String pid = null;
        
        List<String> values = getProperty().getListValue();
        if (values != null) {
            lid = values.get(0);
            pid = values.get(1);
        }
        
        lifecyclesLB = new ListBox();
        for (Iterator iterator = lifecycles.iterator(); iterator.hasNext();) {
            WLifecycle l = (WLifecycle)iterator.next();
            
            lifecyclesLB.addItem(l.getName(), l.getId());
            
            if (l.getId().equals(lid)) {
                lifecycle = l;
                phase = lifecycle.getPhaseById(pid);
            }
        }
        
        lifecyclesLB.addChangeListener(new ChangeListener() {

            public void onChange(Widget arg0) {
                WLifecycle selected = getSelectedLifecycle();
                
                showPhasesForLifecycle(selected);
            }
            
        });
        
        lifecycleTable.setWidget(0, 1, lifecyclesLB);
        
        phaseLB = new ListBox();
        
        // This is a new property
        if (lifecycle == null) {
            lifecycle = getSelectedLifecycle();
        }
        
        showPhasesForLifecycle(lifecycle);
        lifecycleTable.setWidget(1, 1, phaseLB);
    }

    protected WLifecycle getSelectedLifecycle() {
        int idx = lifecyclesLB.getSelectedIndex();
        String id = lifecyclesLB.getValue(idx);
        
        return getLifecycle(id);
    }

    private WLifecycle getLifecycle(String id) {
        for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
            WLifecycle l = (WLifecycle) itr.next();
            
            if (l.getId().equals(id)) {
                return l;
            }
        }
        return null;
    }

    private void showPhasesForLifecycle(WLifecycle lifecycle) {
        for (Iterator<WPhase> iterator = lifecycle.getPhases().iterator(); iterator.hasNext();) {
            WPhase p = iterator.next();
            
            if ((phase == null && lifecycle.getInitialPhase().equals(p))
                || p.getNextPhases().contains(phase) || (phase != null && phase.getNextPhases().contains(p)) || p == phase) {
                phaseLB.addItem(p.getName(), p.getId());
                
                if (p == phase) {
                    phaseLB.setSelectedIndex(phaseLB.getSelectedIndex());
                }
            }
        }
    }

    protected WPhase getSelectedPhase() {
        int idx = phaseLB.getSelectedIndex();
        String id = phaseLB.getValue(idx);
        
        for (Iterator<WPhase> itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase l = itr.next();
            
            if (l.getId().equals(id)) {
                return l;
            }
        }
        return null;
    }

    private void updateLabel() {
        valueLabel.setText(lifecycle.getName() + " - " + phase.getName());
    }
    
    protected void onSave(Object value, Object response) {
        phase = getSelectedPhase();
        lifecycle = getSelectedLifecycle();
        updateLabel();
    }

    protected void onSaveFailure(Throwable caught, AbstractCallback saveCallback) {
        // TODO Auto-generated method stub
        super.onSaveFailure(caught, saveCallback);
    }

    public boolean saveAsCollection() {
        // Lifecycles are weird, we have two values but the Property isn't multivalued
        return true;
    }
    
    
}
