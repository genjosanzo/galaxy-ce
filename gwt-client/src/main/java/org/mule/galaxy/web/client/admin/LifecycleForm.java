package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
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
    private Button deletePhase;
    private Button addBtn;
    private WPhase initialPhase;
    private Button delete;

    public LifecycleForm(AdministrationPanel adminPanel, 
                         WLifecycle l, 
                         boolean add) {
        this.adminPanel = adminPanel;
        this.lifecycle = l;
        this.add = add;
        panel = new FlowPanel();
        panel.setStyleName("lifecycle-form-base");
        
        initWidget(panel);
    }

    public void onShow() {
        panel.clear();
        if (add) {
            panel.add(createTitle("Add Lifecycle"));
            lifecycle.setPhases(new ArrayList());
        } else {
            panel.add(createTitle("Edit Lifecycle " + lifecycle.getName()));
        }

        FlowPanel nameAndPhases = new FlowPanel();
        
        nameTB = new TextBox();
        nameTB.setText(lifecycle.getName());
        nameAndPhases.add(asHorizontal(new Label("Name: "), nameTB));
        
        phases = new ListBox();
        phases.setVisibleItemCount(10);
        if (lifecycle.getPhases() != null) {
            for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
                WPhase p = (WPhase)itr.next();
                
                phases.addItem(p.getName(), p.getId());
            }
        }
        nameAndPhases.add(phases);
        
        addBtn = new Button("Add Phase");
        addBtn.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                addPhase();
            }
        });
        
        deletePhase = new Button("Delete Phase");
        deletePhase.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                deletePhase();
            }
        });
        
        nameAndPhases.add(asHorizontal(addBtn, deletePhase));
        
        save = new Button("Save");
        save.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                save();
            }
        });
        
        delete = new Button("Delete");
        delete.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                delete();
            }
        });
        nameAndPhases.add(asHorizontal(save, delete));
        
        // right side of the panel
        nextPhasesPanel = new FlowPanel();
        phases.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                showNextPhases();
            }
            
        });
        
        // add to main panel
        FlexTable table = new FlexTable();
        table.setWidget(0, 0, nameAndPhases);
        table.setWidget(0, 1, nextPhasesPanel);
        table.setCellSpacing(5);
        table.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        table.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        
        panel.add(table);
    }
    
    protected void addPhase() {
        AddDialog dlg = new AddDialog(this);
        dlg.show();
        dlg.center();
    }
    
    protected void addPhase(String name) {
        WPhase p = new WPhase();
        p.setName(name);
        
        lifecycle.getPhases().add(p);
        phases.addItem(name);
        
        phases.setSelectedIndex(phases.getItemCount());
    }

    protected void deletePhase() {
        WPhase phase = getSelectedPhase();
        if (phase == null) return;
        
        lifecycle.getPhases().remove(phase);
        
        int idx = findPhaseInList(phases, phase.getName());
        phases.removeItem(idx);
        
        for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p2 = (WPhase)itr.next();
            
            if (p2.getNextPhases() != null && p2.getNextPhases().contains(phase)) {
                p2.getNextPhases().remove(phase);
            }
         }
        
        nextPhasesPanel.clear();
    }

    protected void showNextPhases() {
        final WPhase phase = getSelectedPhase();
        if (phase == null) return;
        
        nextPhasesPanel.clear();
        
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
        nextPhasesPanel.add(asHorizontal(new Label("Phase Name: "), phaseNameTB));
        
        final CheckBox initialPhaseCB = new CheckBox();
        initialPhaseCB.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                initialPhase = phase;
            }
        });
        initialPhaseCB.setChecked(initialPhase == phase);
        nextPhasesPanel.add(asHorizontal(new Label("Initial Phase: "), initialPhaseCB));

        nextPhasesPanel.add(new Label("Next Phases:"));
        int i = 0;
        for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p = (WPhase)itr.next();
            
            if (p.equals(phase)) continue;
            
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

    private WPhase getSelectedPhase() {
        int idx = phases.getSelectedIndex();
        
        if (idx == -1) return null;
        
        String id = phases.getValue(idx);
        
        WPhase p = lifecycle.getPhaseById(id);
        if (p == null) {
            p = lifecycle.getPhase(id);
        }
        return p;
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
        if (initialPhase == null) {
            adminPanel.setMessage("You must set one phase as the initial phase before the lifecycle can be saved.");
            return;
        }
        
        disable();
        
        lifecycle.setName(nameTB.getText());
        lifecycle.setInitialPhase(initialPhase);
        
        adminPanel.getRegistryService().saveLifecycle(lifecycle, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                reenable();
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                reenable();
                History.newItem("lifecycles");
                adminPanel.setMessage("Lifecycle was saved.");
            }
            
        });
    }
    
    protected void delete() {
        disable();
        
        adminPanel.getRegistryService().deleteLifecycle(lifecycle.getId(), new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                reenable();
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                reenable();
                History.newItem("lifecycles");
                adminPanel.setMessage("Lifecycle was deleted.");
            }
            
        });
    }

    private void disable() {
        nameTB.setEnabled(false);
        phases.setEnabled(false);
        
        if (nextPhases != null) {
            nextPhases.setEnabled(false);
            phaseNameTB.setEnabled(false);
        }
        
        save.setText("Saving...");
        save.setEnabled(false);
    }

    protected void reenable() {
        nameTB.setEnabled(true);
        phases.setEnabled(true);
        
        nextPhases.setEnabled(true);
        phaseNameTB.setEnabled(true);
        
        save.setText("Save");
        save.setEnabled(true);
    }
    
    public static final class AddDialog extends DialogBox {

        public AddDialog(final LifecycleForm panel) {
            // Set the dialog box's caption.
            setText("Please enter the name of the phase you would like to add:");

            InlineFlowPanel buttonPanel = new InlineFlowPanel();

            final TextBox tb = new TextBox();
            buttonPanel.add(tb);
            
            Button cancel = new Button("Cancel");
            cancel.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    AddDialog.this.hide();
                }
            });

            Button ok = new Button("OK");
            ok.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    AddDialog.this.hide();
                    panel.addPhase(tb.getText());
                }
            });
            buttonPanel.add(tb);
            buttonPanel.add(cancel);
            buttonPanel.add(ok);

            setWidget(buttonPanel);
        }
        
        public void center() {
            final DialogBox dialog = this;
            this.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                public void setPosition(int offsetWidth, int offsetHeight) {
                    int left = (Window.getClientWidth() - offsetWidth) / 3;
                    int top = (Window.getClientHeight() - offsetHeight) / 3;
                    dialog.setPopupPosition(left, top);
                }
            });
        }
    }

}
