package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.mule.galaxy.web.client.util.AbstractForm;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

/**
 * I apologize for the ugliness of this class in advance - there
 * is no way to develop a good UI for this without spaghetti code. - DD
 */
public class LifecycleForm extends AbstractForm {

    private final WLifecycle lifecycle;
    private TextBox nameTB;
    private FlexTable nextPhasesPanel;
    private ListBox phases;
    private ListBox nextPhases;
    private TextBox phaseNameTB;
    private final AdministrationPanel adminPanel;
    private Button deletePhase;
    private Button addBtn;
    private WPhase initialPhase;
    private CheckBox defaultLifecycleCB;

    public LifecycleForm(AdministrationPanel adminPanel,
                         WLifecycle l,
                         boolean newItem) {
        super(adminPanel, newItem, "lifecycles", "Lifecycle was saved.", "Lifecycle was deleted.");

        this.adminPanel = adminPanel;
        this.lifecycle = l;

        panel.setStyleName("lifecycle-form-base");

        initialPhase = l.getInitialPhase();
    }

    protected void addFields(FlexTable table) {
        FlexTable nameAndPhases = createColumnTable();

        nameTB = new TextBox();
        nameTB.setText(lifecycle.getName());
        nameAndPhases.setText(0, 0, "Lifecycle Name:");
        nameAndPhases.setWidget(0, 1, nameTB);

        defaultLifecycleCB = new CheckBox();
        if (lifecycle.isDefaultLifecycle()) {
            nameAndPhases.setText(1, 0, "Is Default Lifecycle:");
            nameAndPhases.setText(1, 1, "Yes");
        } else {
            nameAndPhases.setText(1, 0, "Make Default Lifecycle:");
            nameAndPhases.setWidget(1, 1, defaultLifecycleCB);
        }

        phases = new ListBox();
        phases.setVisibleItemCount(10);
        if (lifecycle.getPhases() != null) {
            for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
                WPhase p = (WPhase) itr.next();

                phases.addItem(p.getName(), p.getId());
            }
        }

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


        FlowPanel addDelPhase = new FlowPanel();
        addDelPhase.add(asDiv(phases));
        addDelPhase.add(asDiv(addBtn));
        addDelPhase.add(asDiv(deletePhase));

        nameAndPhases.setText(2, 0, "Phases:");
        nameAndPhases.setWidget(2, 1, addDelPhase);

        // right side of the panel
        nextPhasesPanel = createColumnTable();
        phases.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                showNextPhases();
            }

        });

        // add to main panel
        styleHeaderColumn(nameAndPhases);
        table.setWidget(0, 0, nameAndPhases);
        table.setWidget(0, 1, nextPhasesPanel);
    }

    public String getTitle() {
        String title;
        if (newItem) {
            title = "Add Lifecycle";
            lifecycle.setPhases(new ArrayList());
        } else {
            title = "Edit Lifecycle " + lifecycle.getName();
        }
        return title;
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
            WPhase p2 = (WPhase) itr.next();

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
        nextPhasesPanel.setText(0, 0, "Phase Name:");
        nextPhasesPanel.setWidget(0, 1, phaseNameTB);

        final CheckBox initialPhaseCB = new CheckBox();
        initialPhaseCB.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                initialPhase = phase;
            }
        });
        initialPhaseCB.setChecked(initialPhase == phase);

        nextPhasesPanel.setText(1, 0, "Initial Phase:");
        nextPhasesPanel.setWidget(1, 1, initialPhaseCB);

        int i = 0;
        for (Iterator itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p = (WPhase) itr.next();

            if (p.equals(phase)) continue;

            nextPhases.addItem(p.getName(), p.getId());

            if (phase.getNextPhases() != null && phase.getNextPhases().contains(p)) {
                nextPhases.setItemSelected(i, true);
            }
            i++;
        }

        nextPhasesPanel.setText(2, 0, "Next Phases:");
        nextPhasesPanel.setWidget(2, 1, nextPhases);

        nextPhases.addChangeListener(new ChangeListener() {
            public void onChange(Widget arg0) {
                updateNextPhases(phase, nextPhases);
            }
        });
        styleHeaderColumn(nextPhasesPanel);
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

        super.save();

        if (defaultLifecycleCB.isChecked()) {
            lifecycle.setDefaultLifecycle(true);
        }
        lifecycle.setName(nameTB.getText());
        lifecycle.setInitialPhase(initialPhase);

        adminPanel.getRegistryService().saveLifecycle(lifecycle, getSaveCallback());
    }

    protected void delete() {
        super.delete();

        adminPanel.getRegistryService().deleteLifecycle(lifecycle.getId(), getDeleteCallback());
    }

    protected void setEnabled(boolean enabled) {
        nameTB.setEnabled(enabled);
        phases.setEnabled(enabled);

        if (nextPhases != null) {
            nextPhases.setEnabled(enabled);
            phaseNameTB.setEnabled(enabled);
        }

        super.setEnabled(enabled);
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

            // allow keyboard shortcuts
            tb.addKeyboardListener(new KeyboardListenerAdapter() {
                public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                    if ((keyCode == KEY_ENTER) && (modifiers == 0)) {
                        AddDialog.this.hide();
                        panel.addPhase(tb.getText());
                    }
                    if ((keyCode == KEY_ESCAPE) && (modifiers == 0)) {
                        AddDialog.this.hide();
                    }
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
