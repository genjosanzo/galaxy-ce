package org.mule.galaxy.repository.client.item;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WType;
import org.mule.galaxy.web.client.ui.AbstractErrorHandlingPopup;
import org.mule.galaxy.web.client.ui.validator.FieldNotEmptyValidator;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemNotFoundException;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;

public class AddWorkspaceForm extends AbstractErrorHandlingPopup {

    private List<WType> types;
    private final RegistryServiceAsync registryService;
    private Button closeBtn;
    private Button submitBtn;

    //private ProgressBar bar;
    private FormData formData;
    private TextField<String> fname;
    private final ItemInfo parent;

    public AddWorkspaceForm(final RegistryServiceAsync registryService,
                            ItemInfo parent) {
        super();
        this.registryService = registryService;
        this.parent = parent;

        formData = new FormData("-20");
        fpanel.setAction(GWT.getModuleBaseURL() + "../artifactUpload.form");

        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading("Add New Workspace");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(75);
        fieldSet.setLayout(layout);

        fpanel.add(fieldSet);

        fname = new TextField<String>();
        fname.setFieldLabel("Name");
        fname.setAllowBlank(false);
        fname.setValidator(new FieldNotEmptyValidator());
//            fname.setName(this.getTypeByName("Artifact").getId());
        fieldSet.add(fname, formData);
        fpanel.add(fieldSet);

        closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        });

        submitBtn = new Button("Add");
        submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                addWorkspace();
            }
        });

        fpanel.addButton(submitBtn);
        fpanel.addButton(closeBtn);
    }

    protected void addWorkspace() {
        setEnabled(false);
        registryService.getTypes(new AbstractCallback<List<WType>>(AddWorkspaceForm.this) {
            public void onSuccess(List<WType> types) {
                addWorkspace(types);
            }
        });

    }

    protected void addWorkspace(List<WType> types) {
        this.types = types;

        AbstractCallback callback = new AbstractCallback(this) {
            public void onSuccess(Object id) {
                History.newItem("item/" + id);
                hide();
            }

            @Override
            public void onFailure(Throwable caught) {
                setEnabled(true);
                if (caught instanceof ItemExistsException) {
                    AddWorkspaceForm.this.setMessage("An item with that name already exists.");
                } else if (caught instanceof ItemNotFoundException) {
                    AddWorkspaceForm.this.setMessage("A workspace with that name could not be found.");
                } else {
                    super.onFailure(caught);
                }
            }
        };

        String parentId = parent != null ? parent.getPath() : null;
        registryService.addItem(parentId,
                fname.getValue(),
                null,
                getTypeByName("Workspace").getId(),
                new HashMap<String, Serializable>(),
                callback);
    }

    private WType getTypeByName(String name) {
        WType artifact = null;
        for (WType type : types) {
            if (name.equals(type.getName())) {
                artifact = type;
            }
        }
        return artifact;
    }

    private void setEnabled(boolean enabled) {
        submitBtn.setEnabled(enabled);
        closeBtn.setEnabled(enabled);
        if (enabled) {
            submitBtn.setText("Add");
        }
    }

}
