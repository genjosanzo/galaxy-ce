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

public class AddNamedItemForm extends AbstractErrorHandlingPopup {

    private List<WType> types;
    private final RegistryServiceAsync registryService;
    private Button closeBtn;
    private Button submitBtn;

    //private ProgressBar bar;
    private FormData formData;
    private TextField<String> fname;
    private final ItemInfo parent;
    private String typeName;

    public AddNamedItemForm(String title, 
                            String typeName,
                            final RegistryServiceAsync registryService,
                            ItemInfo parent) {
        super();
        this.typeName = typeName;
        this.registryService = registryService;
        this.parent = parent;

        formData = new FormData("-20");
        fpanel.setAction(GWT.getModuleBaseURL() + "../artifactUpload.form");

        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading(title);

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
        registryService.getTypes(new AbstractCallback<List<WType>>(AddNamedItemForm.this) {
            public void onCallSuccess(List<WType> types) {
                addWorkspace(types);
            }
        });

    }

    protected void addWorkspace(List<WType> types) {
        this.types = types;

        AbstractCallback callback = new AbstractCallback(this) {
            public void onCallSuccess(Object id) {
                History.newItem("item/" + id);
                hide();
            }

            @Override
            public void onCallFailure(Throwable caught) {
                setEnabled(true);
                if (caught instanceof ItemExistsException) {
                    AddNamedItemForm.this.setMessage("An item with that name already exists.");
                } else if (caught instanceof ItemNotFoundException) {
                    AddNamedItemForm.this.setMessage("A workspace with that name could not be found.");
                } else {
                    super.onFailure(caught);
                }
            }
        };

        String parentId = parent != null ? parent.getPath() : null;
        registryService.addItem(parentId,
                fname.getValue(),
                null,
                getTypeByName(typeName).getId(),
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
