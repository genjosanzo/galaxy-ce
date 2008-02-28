package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

/**
 * I apologize for the ugliness of this class in advance - there
 * is no way to develop a good UI for this without spaghetti code. - DD
 */
public class LifecycleForm extends AbstractComposite {

    private FlowPanel panel;
    private final WLifecycle lifecycle;
    private final boolean add;
    private TextBox nameTB;
    private FlowPanel nextPhasesPanel;
    private Button save;
    private ListBox phases;
    private ListBox nextPhases;
    private TextBox phaseNameTB;
    private final AdministrationPanel adminPanel;
    private String originalName;

    public LifecycleForm(AdministrationPanel adminPanel, 
                         WLifecycle l, 
                         boolean add) {
        this.adminPanel = adminPanel;
        this.lifecycle = l;
        this.originalName = l.getName();
        this.add = add;
        panel = new FlowPanel();
        panel.setStyleName("lifecycle-form-base");
        
        initWidget(panel);
    }

    public void onShow() {
        panel.clear();
        panel.add(createTitle("Edit Lifecycle " + lifecycle.getName()));

        FlowPanel nameAndPhases = new FlowPanel();
        
        nameTB = new TextBox();
        nameTB.setText(lifecycle.getName());
        nameAndPhases.add(asHorizontal(new Label("Name: "), nameTB));
        
        phases = new ListBox();
        phases.setVisibleItemCount(10);
        for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p = (WPhase)itr.next();
            
            phases.addItem(p.getName(), p.getId());
        }
        nameAndPhases.add(phases);
        
        save = new Button("Save");
        save.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                save();
            }
        });
        nameAndPhases.add(save);
        
        // right side of the panel
        nextPhasesPanel = new FlowPanel();
        phases.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                showNextPhases();
            }
            
        });
        
        // add to main panel
        panel.add(asHorizontal(nameAndPhases, nextPhasesPanel));
    }
    
    protected void showNextPhases() {
        int idx = phases.getSelectedIndex();
        if (idx == -1) return;

        nextPhasesPanel.clear();
        
        final WPhase phase = lifecycle.getPhaseById(phases.getValue(idx));

        nextPhases = new ListBox();
        nextPhases.setMultipleSelect(true);
        nextPhases.setVisibleItemCount(10);
        
        phaseNameTB = new TextBox();
        phaseNameTB.setText(phase.getName());
        phaseNameTB.addFocusListener(new FocusListener() {
            public void onFocus(Widget arg0) {
            }

            public void onLostFocus(Widget arg0) {
                String newName = phaseNameTB.getText();
                
                // update left hand phases list with new name
                int idx = findPhaseInList(phases, phase.getName());
                phases.setItemText(idx, newName);
                
                // update next phases list with new name
                idx = findPhaseInList(nextPhases, phase.getName());
                nextPhases.setItemText(idx, newName);
                
                // update actual phase object
                phase.setName(newName);
            }
        });
        nextPhasesPanel.add(asHorizontal(new Label("Name: "), phaseNameTB));
        
        nextPhasesPanel.add(new Label("Next Phases"));
        
        
        int i = 0;
        for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p = (WPhase)itr.next();
            
            nextPhases.addItem(p.getName(), p.getId());
            
            if (phase.getNextPhases() != null && phase.getNextPhases().contains(p)) {
                nextPhases.setItemSelected(i, true);
            }
            i++;
        }
        nextPhasesPanel.add(nextPhases);
        
        nextPhases.addChangeListener(new ChangeListener() {
            public void onChange(Widget arg0) {
                updateNextPhases(phase, nextPhases);
            }
        });
    }

    protected void updateNextPhases(WPhase phase, ListBox nextPhases) {
        phase.setNextPhases(new ArrayList());
        for (int i = 0; i < nextPhases.getItemCount(); i++) {
            if (nextPhases.isItemSelected(i)) {
                phase.getNextPhases().add(lifecycle.getPhaseById(nextPhases.getValue(i)));
            }
        }
    }

    protected int findPhaseInList(ListBox phases, String name) {
        for (int i = 0; i < phases.getItemCount(); i++) {
            String txt = phases.getItemText(i);
            
            if (txt.equals(name)) {
                return i;
            }
        }
        return -1;
    }


    protected void save() {
        nameTB.setEnabled(false);
        phases.setEnabled(false);
        
        nextPhases.setEnabled(false);
        phaseNameTB.setEnabled(false);
        
        save.setText("Saving...");
        save.setEnabled(false);
        
        adminPanel.getRegistryService().saveLifecycle(lifecycle, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                reenable();
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                reenable();
            }
            
        });
    }

    protected void reenable() {
        nameTB.setEnabled(true);
        phases.setEnabled(true);
        
        nextPhases.setEnabled(true);
        phaseNameTB.setEnabled(true);
        
        save.setText("Save");
        save.setEnabled(true);
        
    }

}
