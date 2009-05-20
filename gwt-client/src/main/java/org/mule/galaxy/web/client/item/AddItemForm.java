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

package org.mule.galaxy.web.client.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.property.AbstractPropertyRenderer;
import org.mule.galaxy.web.client.property.ArtifactRenderer;
import org.mule.galaxy.web.client.property.PropertyInterfaceManager;
import org.mule.galaxy.web.client.registry.PolicyResultsPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.ItemPathOracle;
import org.mule.galaxy.web.client.util.PropertyDescriptorComparator;
import org.mule.galaxy.web.client.util.StringUtil;
import org.mule.galaxy.web.client.util.TooltipListener;
import org.mule.galaxy.web.client.util.WTypeComparator;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.rpc.WType;

/**
 * This form is definitely complex and ugly, so here's a run down of how it works.
 * If you select a normal Type (i.e. not an Artifact), it will create editable
 * fields for all those properties using the property renderers. 
 * 
 * Where it gets tricky is when someone selects an artifact. Then we also allow
 * the user the option of submitting an initial version as well. In this case,
 * we follow a three step process:
 * 1. Submit a form which uploads the file. This will get stored in the UploadService
 * on the server side.
 * 2. Create both the artifact and artifact version via RegistryService.addVersionedItem
 * 
 * @author Dan
 *
 */
public class AddItemForm extends AbstractErrorShowingComposite
        implements ClickHandler, SubmitCompleteHandler {

    private FlexTable table;
    private FormPanel form;
    private ValidatableTextBox nameBox;
    private SuggestBox parentSB;
    private final Galaxy galaxy;
    private String itemId;
    private Button addButton;
    private Button cancelButton;
    private ListBox typeChoice;
    protected Map<String,WType> types;
    private PropertyInterfaceManager factory = new PropertyInterfaceManager();
    private ItemInfo item;
    private Map<String, AbstractPropertyRenderer> renderers = new HashMap<String, AbstractPropertyRenderer>();
    private Map<String, AbstractPropertyRenderer> versionRenderers = new HashMap<String, AbstractPropertyRenderer>();
    private Image spacerimg;
    private ValidatableTextBox versionNameBox;
    private boolean addVersionedItem;
    private boolean fileUpload;
    private String fileId;
    private WType avType;
    private CheckBox addVersionCB;
    private boolean fileUploadForVersion;
    
    public AddItemForm(final Galaxy galaxy) {
        this.galaxy = galaxy;

        FlowPanel main = getMainPanel();
        form = new FormPanel();
        form.addSubmitCompleteHandler(this);

        main.add(form);
        initWidget(main);
    }

    public void onHide() {
        form.clear();
    }


    public void onShow(List<String> params) {
        if (params.size() > 0) {
            itemId = params.get(0);
            galaxy.getRegistryService().getItemInfo(itemId, false, new AbstractCallback<ItemInfo>(this) {
                public void onSuccess(ItemInfo item) {
                    AddItemForm.this.item = item;
                    finishShow();
                }
            });
        } else {
            finishShow();
        }
    }
    
    protected void finishShow() {
        form.setAction(GWT.getModuleBaseURL() + "../artifactUpload.form");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        FlowPanel panel = new FlowPanel();
        form.add(panel);

        panel.add(createPrimaryTitle("Add Item"));

        table = createColumnTable();
        panel.add(table);

        setupAddForm();
        
        this.onShow();
    }


    private void setupAddForm() {

        typeChoice = new ListBox();
        typeChoice.addItem("");
        table.setWidget(0, 0, new Label("Type:"));
        table.setWidget(0, 2, typeChoice);
        
        // note how spacing uses a clear pixel on the second column
        table.setWidget(1, 0, new Label("Parent:"));

        parentSB = new SuggestBox(new ItemPathOracle(galaxy, this));
        parentSB.getTextBox().setName("workspacePath");
        if (item != null) {
            parentSB.setText(item.getPath());
        }
        table.setWidget(1, 2, parentSB);

        table.setWidget(2, 0, new Label("Name:"));

        // to control formatting
        spacerimg = new Image("images/clearpixel.gif");
        spacerimg.setWidth("16px");
        table.setWidget(2, 1, spacerimg);

        nameBox = new ValidatableTextBox(new StringNotEmptyValidator());
        nameBox.getTextBox().setName("name");
        table.setWidget(2, 2, nameBox);

        galaxy.getRegistryService().getTypes(new AbstractCallback<List<WType>>(this) {
            public void onSuccess(List<WType> wtypes) {
                Collections.sort(wtypes, new WTypeComparator());
                AddItemForm.this.types = new HashMap<String,WType>();
                for (WType type : wtypes) {
                    types.put(type.getId(), type);
                    typeChoice.addItem(type.getName(), type.getId());
                }
            }
        });
        
        typeChoice.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                WType type = getType();
                if (type != null) {
                    selectType(type);
                }
            }
        });
        
        setupTableBottom(false);
    }

    /**
     * When a user selects an artifact, we also want to let them add the child type
     * ArtifactVersion all in one go. This will set up a second piece of the form 
     * to deal with that.
     */
    private void setupVersionItemForm(boolean addCheckbox) {
        int row = table.getRowCount();

        if (addCheckbox) {
            // blank row for spacing
            table.setWidget(row, 2, newSpacer());
            
            row ++;
            table.setWidget(row, 0, new Label("Add Version:"));
            addVersionCB = new CheckBox();
            addVersionCB.setValue(true);
    
            final int versionRowStart = row + 1;
            addVersionCB.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent arg0) {
                    showOrHideVersion(versionRowStart, addVersionCB.getValue());
                }
            });
            table.setWidget(row, 2, addVersionCB);
            row++;
        }
        
        table.setWidget(row, 0, new Label("Version:"));

        versionNameBox = new ValidatableTextBox(new StringNotEmptyValidator());
        table.setWidget(row, 2, versionNameBox);
        
        // warn user if the artifact name does not contain an extention.
        nameBox.getTextBox().addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                String s = ((TextBox) event.getSource()).getText();
                if (s.length() > 0 && StringUtil.getFileExtension(s).length() == 0) {
                    // warn icon and tooltip
                    Image warnimg = new Image("images/icon_alert.gif");
                    warnimg.addMouseListener(new TooltipListener(" Warning: Artifact name does not contain a valid file extension. " +
                            "Galaxy will not be able to reliably detect the artifact type. You may leave this field empty" +
                            "if you want retain the uploaded filename",
                                                                 10000));

                    table.setWidget(2, 1, warnimg);
                } else {
                    table.setWidget(2, 1, spacerimg);
                }
            }
        });

        avType = getTypeByName("Artifact Version");
        addRenderers(avType.getProperties(), versionRenderers, true);
    }

    protected void showOrHideVersion(int versionRowStart, Boolean showVersion) {
        for (int i = table.getRowCount()-1; i >= versionRowStart; i--) 
            table.removeRow(i);
        
        if (showVersion) {
            setupVersionItemForm(false);
        }
        
        setupTableBottom(true);
    }

    protected WType getType() {
        String val = typeChoice.getValue(typeChoice.getSelectedIndex());
        if (!"".equals(val)) {
            return types.get(val);
        }
        return null;
    }

    protected void selectType(WType type) {
        List<WPropertyDescriptor> props = type.getProperties();
        Collections.sort(props, new PropertyDescriptorComparator());
        
        for (int i = table.getRowCount()-1; i >= 3; i--) 
            table.removeRow(i);
        
        fileUpload = false;
        renderers.clear();
        versionRenderers.clear();
        
        // setup the custom fields for the Type
        addRenderers(props, renderers, false);
        
        if (isArtifact(type)) {
            addVersionedItem = true;
            setupVersionItemForm(true);
        } else {
            addVersionedItem = false;
        }
        
        setupTableBottom(true);
    }

    private void addRenderers(List<WPropertyDescriptor> props,
                              Map<String, AbstractPropertyRenderer> typeRenderers,
                              boolean version) {
        int row = table.getRowCount();
        for (WPropertyDescriptor pd : props) {
            table.setText(row, 0, pd.getDescription());
            AbstractPropertyRenderer renderer = factory.createRenderer(pd.getExtension(), pd.isMultiValued());
            renderer.initialize(galaxy, this, null, false);
            typeRenderers.put(pd.getName(), renderer);
            table.setWidget(row, 2, renderer.createEditForm());
            
            if (renderer instanceof ArtifactRenderer) {
                if (version) {
                    fileUploadForVersion = true;
                } else {
                    fileUpload = true;
                }
            }
            row++;
        }
    }

    /**
     * Does this type inherit from the artifact type?
     */
    private boolean isArtifact(WType selectedType) {
        WType artifact = getTypeByName("Artifact");
        
        if (artifact == null) {
            return false;
        }
        
        if (selectedType.getId().equals(artifact.getId())) {
            return true;
        }

        
        return false;
    }

    private WType getTypeByName(String name) {
        WType artifact = null;
        for (WType type: types.values()) {
            if (name.equals(type.getName())) {
                artifact = type; 
            }
        }
        return artifact;
    }

    private void setupTableBottom(boolean showAdd) {
        table.setWidget(table.getRowCount(), 2, newSpacer());
        
        InlineFlowPanel buttons = new InlineFlowPanel();
        if (showAdd) {
            addButton = new Button("Add");
            addButton.addClickHandler(this);
            buttons.add(addButton);
        }
        
        cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(this);

        buttons.add(cancelButton);

        table.setWidget(table.getRowCount(), 2, buttons);

        setTitle("Add Item");
        
        styleHeaderColumn(table);
    }


    public void onSubmitComplete(SubmitCompleteEvent event) {
        String msg = event.getResults();

        // some platforms insert css info into the pre-tag -- just remove it all
        msg = msg.replaceAll("\\<.*?\\>", "");

        // This is our 200 OK response
        // eg:  OK 9c495a52-4a07-4697-ba73-f94f95cd3020
        if (msg.startsWith("OK ")) {
            fileId = msg.substring(3);
            
            // Once we've uploaded the artifact, continue with the normal RPC
            // submission process.
            addItem();
        } else {
            this.setMessage(msg);
            resetFormFields();
        }
    }


    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        if (sender == addButton) {
            add();
        }

        if (sender == cancelButton) {
            cancel();
        }
    }

    private void cancel() {
        String token = "browse";
        if (item != null) {
            token += "/" + item.getId();
        }
        History.newItem(token);
    }

    private void add() {
        // block uploads once the addButton is pressed
        addButton.setEnabled(false);

        // last chance to validate
        if (!validate()) {
            resetFormFields();
            return;
        }

        // whitespace will throw an invalid path exception
        // on the server -- so trim this optional value
        if (nameBox != null) {
            String name = nameBox.getText().trim();
            if (name != null || !"".equals(name)) {
                nameBox.setText(name);
            }
        }
        
        doSubmit();
    }

    /**
     * Check to see if we need to do a form submission to upload files. If not,
     * or once that is done, move on to adding new items. 
     */
    private void doSubmit() {
        if (fileUpload || (fileUploadForVersion && isUserSubmittingVersion())) {
            form.submit();
        } else {
            addItem();
        }
    }

    private void addItem() {
        AbstractCallback callback = geAddItemCallback();
        
        String name = nameBox. getText();
        String parent = parentSB.getText();
        Map<String, Serializable> properties = getProperties(renderers);
        
        if (isUserSubmittingVersion()) {
            galaxy.getRegistryService().addVersionedItem(parent, 
                                                         name,
                                                         versionNameBox.getText(),
                                                         null, 
                                                         getType().getId(), 
                                                         avType.getId(),
                                                         properties, 
                                                         getProperties(versionRenderers),
                                                         callback);
        } else {
            galaxy.getRegistryService().addItem(parent, 
                                                name,
                                                null, 
                                                getType().getId(), 
                                                properties, 
                                                callback);
        }
        
    }

    private Map<String, Serializable> getProperties(Map<String, AbstractPropertyRenderer> typeRenderers) {
        Map<String,Serializable> properties = new HashMap<String, Serializable>();
        for (String p : typeRenderers.keySet()) {
            AbstractPropertyRenderer r = typeRenderers.get(p);
            if (r instanceof ArtifactRenderer) {
                properties.put(p, fileId);
            } else {
                properties.put(p, (Serializable)r.getValueToSave());
            }
        }
        return properties;
    }

    private AbstractCallback geAddItemCallback() {
        AbstractCallback callback = new AbstractCallback(this) {
            public void onSuccess(Object id) {
                 History.newItem("item/" + id);
            }

            @Override
            public void onFailure(Throwable caught) {
                resetFormFields();
                
                if (caught instanceof ItemExistsException) {
                    setMessage("An item with that name already exists.");
                    return;
                }
                
                super.onFailure(caught);
            }
            
        };
        return callback;
    }

    protected void parseAndShowPolicyMessages(String msg) {
        String[] split = msg.split("\n");

        List<String> warnings = new ArrayList<String>();
        List<String> failures = new ArrayList<String>();
        String lines = null;
        boolean warning = true;
        for (int i = 1; i < split.length; i++) {
            String s = split[i];

            if (s.startsWith("WARNING: ")) {
                addWarningOrFailure(warnings, failures, lines, warning);

                warning = true;
                lines = getMessage(s);
            } else if (s.startsWith("FAILURE: ")) {
                addWarningOrFailure(warnings, failures, lines, warning);

                warning = false;
                lines = getMessage(s);
            } else {
                lines += s;
            }
        }

        addWarningOrFailure(warnings, failures, lines, warning);

        String token = "policy-failures";
        if (itemId != null) {
            token += "-" + itemId;
        }
        PolicyResultsPanel failurePanel = new PolicyResultsPanel(galaxy, warnings, failures);
        failurePanel.setMessage("The artifact did not meet all the necessary policies!");
        galaxy.createPageInfo(token, failurePanel, 0);
        History.newItem(token);
    }


    private void addWarningOrFailure(List<String> warnings, List<String> failures, String lines, boolean warning) {
        if (lines == null) return;

        if (warning) {
            warnings.add(lines);
        } else {
            failures.add(lines);
        }
    }


    private String getMessage(String s) {
        s = s.substring(9);
        return s;
    }


    private boolean validate() {
        clearErrorMessage();
        boolean v = true;

        v &= nameBox.validate();
        
        for (AbstractPropertyRenderer r : renderers.values()) {
            v &= r.validate();
        }
        
        if (isUserSubmittingVersion()) {
            for (AbstractPropertyRenderer r : versionRenderers.values()) {
                v &= r.validate();
            }
        }
        return v;
    }

    /**
     * Whether or not there is a versioned item to submit and the "Add Version" checkbox
     * is checked. 
     * @return
     */
    private boolean isUserSubmittingVersion() {
        return addVersionedItem && addVersionCB.getValue();
    }

    private void resetFormFields() {
        addButton.setText("Add");
        addButton.setEnabled(true);
    }

    public void setItem(ItemInfo item) {
        this.item = item;
    }
}
