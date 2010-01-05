package org.mule.galaxy.repository.client.property;

import java.io.Serializable;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.client.admin.PolicyPanel;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WPolicyException;
import org.mule.galaxy.repository.rpc.WProperty;
import org.mule.galaxy.web.client.AbstractShowable;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Encapsulates the rendering and editing of a property value.
 */
public class EditPropertyPanel extends AbstractShowable {

    private Button save;
    protected Button cancel;
    private AbstractPropertyRenderer renderer;
    protected InlineFlowPanel panel;
    protected ErrorPanel errorPanel;
    protected String itemId;
    protected WProperty property;
    protected RegistryServiceAsync registryService;
    protected ClickListener saveListener;
    protected ClickListener deleteListener;
    protected ClickListener cancelListener;
    private RepositoryModule repositoryModule;

    public EditPropertyPanel(AbstractPropertyRenderer renderer, ErrorPanel errorPanel) {
        super();

        this.panel = new InlineFlowPanel();

        initWidget(panel);
        this.renderer = renderer;
        this.errorPanel = errorPanel;
    }

    public void initialize() {
        initializeRenderer();
    }

    public InlineFlowPanel createViewPanel() {
        Image editImg = new Image("images/page_edit.gif");
        editImg.setStyleName("icon-baseline");
        editImg.setTitle("Edit");
        editImg.addClickListener(new ClickListener() {

            public void onClick(Widget widget) {
                showEdit();
            }

        });


        Image deleteImg = new Image("images/delete_config.gif");
        deleteImg.setStyleName("icon-baseline");
        deleteImg.setTitle("Delete");
        deleteImg.addClickListener(new ClickListener() {

            public void onClick(Widget widget) {
                delete();
            }

        });

        InlineFlowPanel viewPanel = new InlineFlowPanel();
        viewPanel.add(renderer.createViewWidget());

        if (!property.isLocked()) {
            // interesting... spacer label has to be a new object ref, otherwise not honored...
            viewPanel.add(new Label(" "));
            viewPanel.add(editImg);
            viewPanel.add(new Label(" "));
            viewPanel.add(deleteImg);
        }
        return viewPanel;
    }

    protected FlowPanel createEditPanel() {
        FlowPanel editPanel = new FlowPanel();
        editPanel.setStyleName("add-property-inline");

        Widget editForm = renderer.createEditForm();
        editPanel.add(editForm);

        FlowPanel buttonPanel = new FlowPanel();
        cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                cancel();
            }

        });

        if (cancelListener != null) {
            cancel.addClickListener(cancelListener);
        }

        save = new Button("Save");
        save.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                cancel.setEnabled(false);
                save.setEnabled(false);

                save();
            }

        });

        buttonPanel.add(save);
        buttonPanel.add(cancel);

        editPanel.add(buttonPanel);

        return editPanel;
    }

    protected void cancel() {
        errorPanel.clearErrorMessage();
        initializeRenderer();
        showView();
    }

    public void showView() {
        panel.clear();
        panel.add(createViewPanel());
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    doDelete();
                }
            }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete this property?", l);

    }

    protected void doDelete() {
        registryService.deleteProperty(itemId, property.getName(), new AbstractCallback(errorPanel) {

            public void onSuccess(Object arg0) {
                deleteListener.onClick(null);
            }

        });
    }

    public void showEdit() {
        panel.clear();

        FlowPanel editPanel = createEditPanel();
        panel.add(editPanel);
    }

    protected void save() {
        final Serializable value = (Serializable) renderer.getValueToSave();

        AbstractCallback saveCallback = getSaveCallback(value);

        setEnabled(false);

        renderer.save(itemId, property.getName(), value, saveCallback);

    }

    protected AbstractCallback getSaveCallback(final Serializable value) {
        AbstractCallback saveCallback = new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof WPolicyException) {
                    WPolicyException pe = (WPolicyException) caught;

                    PolicyPanel.handlePolicyFailure(repositoryModule.getGalaxy(), pe);
                } else {
                    onSaveFailure(caught, this);
                }
            }

            public void onSuccess(Object response) {
                onSave(value, response);
            }

        };
        return saveCallback;
    }

    protected void onSave(final Serializable value, Object response) {
        setEnabled(true);
        property.setValue(value);

        initializeRenderer();

        showView();

        if (saveListener != null) {
            saveListener.onClick(save);
        }
    }

    private void initializeRenderer() {
        renderer.initialize(repositoryModule, errorPanel, property.getValue(), false);
    }

    protected void onSaveFailure(Throwable caught, AbstractCallback saveCallback) {
        saveCallback.onFailureDirect(caught);
        setEnabled(true);
    }

    public WProperty getProperty() {
        return property;
    }

    public void setProperty(WProperty property) {
        this.property = property;
    }

    public void setErrorPanel(ErrorPanel errorPanel) {
        this.errorPanel = errorPanel;
    }

    public void setItemId(String entryid) {
        this.itemId = entryid;
    }

    public void setEnabled(boolean b) {
        if (cancel != null) {
            cancel.setEnabled(b);
        }
        if (save != null) {
            save.setEnabled(b);
        }
    }

    public void setSaveListener(ClickListener saveListener) {
        this.saveListener = saveListener;
    }

    public void setDeleteListener(ClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setCancelListener(ClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setRepositoryModule(RepositoryModule repositoryModule) {
        this.repositoryModule = repositoryModule;
    }
}
