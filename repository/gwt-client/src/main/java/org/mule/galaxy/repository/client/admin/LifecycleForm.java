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

package org.mule.galaxy.repository.client.admin;

import java.util.ArrayList;
import java.util.Iterator;

import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WLifecycle;
import org.mule.galaxy.repository.rpc.WPhase;
import org.mule.galaxy.web.client.ui.field.ValidatableTextBox;
import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.client.admin.AbstractAdministrationForm;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.panel.InlineHelpPanel;
import org.mule.galaxy.web.client.ui.dialog.LightBox;
import org.mule.galaxy.web.client.ui.validator.StringNotEmptyValidator;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;


public class LifecycleForm extends AbstractAdministrationForm {

    private WLifecycle lifecycle;
    private TextField<String> nameTB;
    private FlexTable nextPhasesPanel;
    private ListBox phases;
    private ListBox nextPhases;
    private ValidatableTextBox phaseNameTB;
    private Button deletePhase;
    private Button addBtn;
    private WPhase initialPhase;
    private CheckBox defaultLifecycleCB;
    private static Button ok;
    private static Button cancel;
    private final RegistryServiceAsync registryService;

    public LifecycleForm(AdministrationPanel adminPanel, RegistryServiceAsync registryService) {
        super(adminPanel, "lifecycles", "Lifecycle was saved.", "Lifecycle was deleted.",
                "A lifecycle with that name already exists");
        this.registryService = registryService;

        panel.setStyleName("lifecycle-form-base");
    }

    protected void fetchItem(String id) {
        registryService.getLifecycle(id, getFetchCallback());
    }

    protected void initializeItem(Object o) {
        lifecycle = (WLifecycle) o;

        initialPhase = lifecycle.getInitialPhase();
    }

    protected void initializeNewItem() {
        lifecycle = new WLifecycle();
        initialPhase = null;
    }

    protected FlexTable createFormTable() {
        FlexTable table = new FlexTable();

        table.setCellSpacing(5);
        table.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        table.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

        return table;
    }

    protected void addFields(FlexTable table) {
        FlexTable nameAndPhases = createColumnTable();

        nameTB = new TextField<String>();
        nameTB.setAllowBlank(false);
        nameTB.setValue(lifecycle.getName());
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
            for (Iterator<WPhase> itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
                WPhase p = itr.next();

                phases.addItem(p.getName(), p.getId());
            }
        }

        SelectionListener listener = new SelectionListener<ComponentEvent>() {
            public void componentSelected(ComponentEvent ce) {
                Button btn = (Button) ce.getComponent();

                if (btn == addBtn) {
                    addPhase();
                }
                if (btn == deletePhase) {
                    deletePhase();
                }

            }
        };

        addBtn = new Button("Add", listener);
        deletePhase = new Button("Delete", listener);

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
            lifecycle.setPhases(new ArrayList<WPhase>());
            setHelpPanel(new InlineHelpPanel(
                    galaxy.getAdministrationConstants().admin_AddLifecycle_Tip(), 22));
        } else {
            title = "Edit Lifecycle " + lifecycle.getName();
            setHelpPanel(new InlineHelpPanel(
                    galaxy.getAdministrationConstants().admin_EditLifecycle_Tip(), 22));
        }
        return title;
    }


    protected void addPhase() {
        new LightBox(new AddDialog(this)).show();
    }

    protected void addPhase(String name) {
        WPhase p = new WPhase();
        p.setName(name);

        lifecycle.getPhases().add(p);
        phases.addItem(name);

        phases.setSelectedIndex(phases.getItemCount() - 1);
        showNextPhases();
    }

    protected void deletePhase() {
        WPhase phase = getSelectedPhase();
        if (phase == null) return;

        lifecycle.getPhases().remove(phase);

        int idx = findPhaseInList(phases, phase.getName());
        phases.removeItem(idx);

        for (Iterator<WPhase> itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p2 = itr.next();

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

        phaseNameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        phaseNameTB.getTextBox().setText(phase.getName());
        phaseNameTB.getTextBox().addFocusListener(new FocusListener() {
            public void onFocus(Widget arg0) {
            }

            public void onLostFocus(Widget arg0) {
                if (!phaseNameTB.validate()) {
                    phaseNameTB.getTextBox().setFocus(true);
                    return;
                }
                String newName = phaseNameTB.getTextBox().getText();

                // update left hand phases list with new name
                int idx = findPhaseInList(phases, phase.getName());
                phases.setItemText(idx, newName);
                phases.setValue(idx, newName);

                // update next phases list with new name
                idx = findPhaseInList(nextPhases, phase.getName());
                if (idx != -1) {
                    nextPhases.setItemText(idx, newName);
                    nextPhases.setValue(idx, newName);
                }

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
        for (Iterator<WPhase> itr = lifecycle.getPhases().iterator(); itr.hasNext();) {
            WPhase p = itr.next();

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
        phase.setNextPhases(new ArrayList<WPhase>());
        for (int i = 0; i < nextPhases.getItemCount(); i++) {
            if (nextPhases.isItemSelected(i)) {
                String pName = nextPhases.getItemText(i);

                phase.getNextPhases().add(lifecycle.getPhase(pName));
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
        if (!validate()) {
            return;
        }

        super.save();

        if (defaultLifecycleCB.isChecked()) {
            lifecycle.setDefaultLifecycle(true);
        }
        lifecycle.setName(nameTB.getValue());
        lifecycle.setInitialPhase(initialPhase);

        registryService.saveLifecycle(lifecycle, getSaveCallback());
    }

    protected void delete() {
        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    LifecycleForm.super.delete();
                    registryService.deleteLifecycle(lifecycle.getId(), getDeleteCallback());
                }
            }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete lifecycle " + lifecycle.getName() + "?", l);
    }

    protected void setEnabled(boolean enabled) {
        nameTB.setEnabled(enabled);
        phases.setEnabled(enabled);

        if (nextPhases != null) {
            nextPhases.setEnabled(enabled);
            phaseNameTB.getTextBox().setEnabled(enabled);
        }

        super.setEnabled(enabled);
    }

    public static final class AddDialog extends DialogBox {

        public AddDialog(final LifecycleForm panel) {
            // Set the dialog box's caption.
            setText("Please enter the name of the phase you would like to add:");

            FlexTable buttonPanel = new FlexTable();

            final ValidatableTextBox tb = new ValidatableTextBox(new StringNotEmptyValidator());


            SelectionListener listener = new SelectionListener<ComponentEvent>() {
                public void componentSelected(ComponentEvent ce) {
                    Button btn = (Button) ce.getComponent();

                    if (btn == cancel) {
                        AddDialog.this.hide();
                    }
                    if (btn == ok) {
                        if (!tb.validate()) {
                            return;
                        }
                        AddDialog.this.hide();
                        panel.addPhase(tb.getTextBox().getText());
                    }

                }
            };


            cancel = new Button("Cancel", listener);
            ok = new Button("OK", listener);

            // allow keyboard shortcuts
            tb.getTextBox().addKeyboardListener(new KeyboardListenerAdapter() {
                public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                    if ((keyCode == KEY_ENTER) && (modifiers == 0)) {
                        if (!tb.validate()) {
                            return;
                        }
                        AddDialog.this.hide();
                        panel.addPhase(tb.getTextBox().getText());
                    }
                    if ((keyCode == KEY_ESCAPE) && (modifiers == 0)) {
                        AddDialog.this.hide();
                    }
                }
            });

            buttonPanel.setWidget(0, 0, tb);
            buttonPanel.setWidget(0, 1, ok);
            buttonPanel.setWidget(0, 2, cancel);

            setWidget(buttonPanel);
        }
    }

    protected boolean validate() {
        final ErrorPanel errorPanel = getErrorPanel();
        errorPanel.clearErrorMessage();

        boolean isOk = true;
        if (initialPhase == null) {
            errorPanel.addMessage("You must set one phase as the initial phase before the lifecycle can be saved.");
            isOk = false;
        }

        if (phases.getItemCount() == 0) {
            errorPanel.addMessage("Lifecycle must have at least one phase");
            isOk = false;
        }

        isOk &= nameTB.validate();

        if (phaseNameTB != null) {
            isOk &= phaseNameTB.validate();
        }

        return isOk;
    }
}