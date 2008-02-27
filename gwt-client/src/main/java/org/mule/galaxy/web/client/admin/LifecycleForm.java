package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

public class LifecycleForm extends AbstractComposite {

    private FlowPanel panel;
    private final WLifecycle lifecycle;
    private final boolean add;
    private TextBox nameTB;
    private FlowPanel nextPhasesPanel;
    private Button save;
    private ListBox phases;

    public LifecycleForm(AdministrationPanel adminPanel, 
                         WLifecycle l, 
                         boolean add) {
        this.lifecycle = l;
        this.add = add;
        panel = new FlowPanel();
        
        initWidget(panel);
    }

    public void onShow() {
        FlowPanel nameAndPhases = new FlowPanel();
        nameAndPhases.add(new Label("Lifecycle"));
        
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

    protected void save() {
        // TODO Auto-generated method stub
        
    }

    protected void showNextPhases() {
        int idx = phases.getSelectedIndex();
        if (idx == -1) return;

        nextPhasesPanel.clear();
        
        final WPhase phase = findPhase(phases.getItemText(idx));
        
        final TextBox phaseNameTB = new TextBox();
        phaseNameTB.setText(phase.getName());
        phaseNameTB.addFocusListener(new FocusListener() {
            public void onFocus(Widget arg0) {
            }

            public void onLostFocus(Widget arg0) {
                String newName = phaseNameTB.getText();
                int idx = findPhaseInList(phase.getName());
                
                phase.setName(newName);
                phases.setItemText(idx, newName);
            }
        });
        nextPhasesPanel.add(asHorizontal(new Label("Name: "), phaseNameTB));
        
        nextPhasesPanel.add(new Label("Next Phases"));
        
        ListBox nextPhases = new ListBox();
        nextPhases.setMultipleSelect(true);
        nextPhases.setVisibleItemCount(10);
        
        int i = 0;
        for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p = (WPhase)itr.next();
            
            nextPhases.addItem(p.getName(), p.getId());
            
            if (phase.getNextPhases().contains(p)) {
                nextPhases.setItemSelected(i, true);
            }
            i++;
        }
        nextPhasesPanel.add(nextPhases);
    }

    protected int findPhaseInList(String name) {
        for (int i = 0; i < phases.getItemCount(); i++) {
            String txt = phases.getItemText(i);
            
            if (txt.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private WPhase findPhase(String value) {
        for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p = (WPhase)itr.next();
            
            if (p.getName().equals(value)) {
                return p;
            }
        }
        
        return null;
    }

}
