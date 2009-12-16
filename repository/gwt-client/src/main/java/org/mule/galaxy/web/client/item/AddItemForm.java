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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.property.AbstractPropertyRenderer;
import org.mule.galaxy.web.client.property.ArtifactRenderer;
import org.mule.galaxy.web.client.property.PropertyInterfaceManager;
import org.mule.galaxy.web.client.registry.PolicyResultsPanel;
import org.mule.galaxy.web.client.ui.help.InlineHelpPanel;
import org.mule.galaxy.web.client.util.AddItemHelper;
import org.mule.galaxy.web.client.util.ItemPathOracle;
import org.mule.galaxy.web.client.util.StringUtil;
import org.mule.galaxy.web.client.util.TooltipListener;
import org.mule.galaxy.web.client.util.WTypeComparator;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.rpc.WType;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * This form is definitely complex and ugly, so here's a run down of how it works.
 * If you select a normal Type (i.e. not an Artifact), it will create editable
 * fields for all those properties using the property renderers.
 * <p/>
 * Where it gets tricky is when someone selects an artifact. Then we also allow
 * the user the option of submitting an initial version as well. In this case,
 * we follow a three step process:
 * 1. Submit a form which uploads the file. This will get stored in the UploadService
 * on the server side.
 * 2. Create both the artifact and artifact version via RegistryService.addVersionedItem
 *
 * @author Dan
 */
public class AddItemForm extends AbstractFlowComposite implements SubmitCompleteHandler {

    private FlexTable table;
    private TextField<String> nameBox;
    private SuggestBox parentSB;
    private final Galaxy galaxy;
    private String itemId;
    private Button addButton;
    private Button cancelButton;
    private ListBox typeChoice;
    protected Map<String, WType> types;
    private PropertyInterfaceManager factory = new PropertyInterfaceManager();
    private ItemInfo item;
    private Map<String, AbstractPropertyRenderer> renderers = new HashMap<String, AbstractPropertyRenderer>();
    private Map<String, AbstractPropertyRenderer> versionRenderers = new HashMap<String, AbstractPropertyRenderer>();
    private Image spacerimg;
    private TextField<String> versionNameBox;
    private boolean addVersionedItem;
    private boolean fileUpload;
    private String fileId;
    private CheckBox addVersionCB;
    private boolean fileUploadForVersion;
    private ListBox versionTypes;
    private WType selectedVersion;
    private AddItemHelper form;
    private final ErrorPanel errorPanel;

    public AddItemForm(final Galaxy galaxy, ErrorPanel errorPanel) {
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;


        form = new AddItemHelper(galaxy);
        form.setAction(GWT.getModuleBaseURL() + "../artifactUpload.form");
        form.addSubmitCompleteHandler(this);

        ContentPanel cp = new ContentPanel();
        cp.setHeading("Add Item");
        cp.setBodyBorder(false);
        cp.setAutoWidth(true);
        cp.setStyleName("x-panel-container-full");
        cp.add(form);


        // add inline help string and widget
        cp.setTopComponent(
                new InlineHelpPanel(galaxy.getRepositoryConstants().repo_Add_Item_Tip(), 21));

        panel.add(cp);
    }

    public void hidePage() {
        form.clear();
    }


    @Override
    public void showPage(List<String> params) {
        if (params.size() > 0) {
            itemId = params.get(0);
            galaxy.getRegistryService().getItemInfo(itemId, false, new AbstractCallback<ItemInfo>(errorPanel) {
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
        FlowPanel panel = new FlowPanel();
        form.add(panel);

        //panel.add(createPrimaryTitle("Add Item"));

        table = createColumnTable();
        panel.add(table);

        setupAddForm();
    }


    private void setupAddForm() {

        typeChoice = new ListBox();
        typeChoice.addItem("");
        table.setWidget(0, 0, new Label("Type:"));
        table.setWidget(0, 2, typeChoice);

        // note how spacing uses a clear pixel on the second column
        table.setWidget(1, 0, new Label("Parent:"));

        parentSB = new SuggestBox(new ItemPathOracle(galaxy, errorPanel));
        parentSB.setStyleName("x-form-text");

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

        nameBox = new TextField<String>();
        nameBox.setAllowBlank(false);
        nameBox.setName("name");
        table.setWidget(2, 2, nameBox);

        galaxy.getRegistryService().getTypes(new AbstractCallback<List<WType>>(errorPanel) {
            public void onSuccess(List<WType> wtypes) {
                Collections.sort(wtypes, new WTypeComparator());
                AddItemForm.this.types = new HashMap<String, WType>();
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
    private void setupVersionItemForm(boolean redrawAddVersionCB) {
        int row = table.getRowCount();

        if (redrawAddVersionCB) {
            // blank row for spacing
            table.setWidget(row, 2, newSpacer());

            row++;
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

        table.setWidget(row, 0, new Label("Version Type:"));
        versionTypes = new ListBox();
        WType avType = getTypeByName("Version");
        for (WType type : types.values()) {
            if (type.inherits(avType, types)) {
                versionTypes.addItem(type.getName(), type.getId());
                if (selectedVersion == null) {
                    selectedVersion = type;
                } else if (type.getId().equals(selectedVersion.getId())) {
                    versionTypes.setSelectedIndex(versionTypes.getItemCount() - 1);
                }
            }
        }

        // Update the applicable fields if someone selects a new version type
        final int versionRowStart = row;
        versionTypes.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                selectedVersion = getVersionType();
                removeVersionFields(versionRowStart);
                setupVersionItemForm(false);
                setupTableBottom(true);
            }
        });
        table.setWidget(row, 2, versionTypes);

        row++;
        table.setWidget(row, 0, new Label("Version:"));
        versionNameBox = new TextField<String>();
        versionNameBox.setAllowBlank(false);
        table.setWidget(row, 2, versionNameBox);

        nameBox.addListener(Events.Blur, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {

                String s = nameBox.getValue();
                if (s.length() > 0 && StringUtil.getFileExtension(s).length() == 0) {
                    // warn icon and tooltip
                    Image warning = new Image("images/icon_alert.gif");
                    nameBox.setToolTip("Warning: Artifact name does not contain a valid file extension. \" +\n" +
                            "                            \"Galaxy will not be able to reliably detect the artifact type. You may leave this field empty\" +\n" +
                            "                            \"if you want retain the uploaded filename");

                    warning.addMouseListener(new TooltipListener(" Warning: Artifact name does not contain a valid file extension. " +
                            "Galaxy will not be able to reliably detect the artifact type. You may leave this field empty" +
                            "if you want retain the uploaded filename",
                            10000));

                    table.setWidget(2, 1, warning);
                } else {
                    table.setWidget(2, 1, spacerimg);
                }

            }
        });
        addRenderers(selectedVersion.getAllProperties(types), versionRenderers, true);
    }

    protected void showOrHideVersion(int versionRowStart, Boolean showVersion) {
        removeVersionFields(versionRowStart);

        if (showVersion) {
            setupVersionItemForm(false);
        }

        setupTableBottom(true);
    }

    private void removeVersionFields(int versionRowStart) {
        for (int i = table.getRowCount() - 1; i >= versionRowStart; i--)
            table.removeRow(i);
    }

    protected WType getType() {
        String val = typeChoice.getValue(typeChoice.getSelectedIndex());
        if (!"".equals(val)) {
            return types.get(val);
        }
        return null;
    }

    protected WType getVersionType() {
        String val = versionTypes.getValue(versionTypes.getSelectedIndex());
        if (!"".equals(val)) {
            return types.get(val);
        }
        return null;
    }

    protected void selectType(WType type) {
        List<WPropertyDescriptor> props = type.getAllProperties(types);

        for (int i = table.getRowCount() - 1; i >= 3; i--)
            table.removeRow(i);

        fileUpload = false;
        renderers.clear();
        versionRenderers.clear();

        // setup the custom fields for the Type
        addRenderers(props, renderers, false);

        if (isVersioned(type)) {
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
            renderer.initialize(galaxy, errorPanel, null, false);
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
    private boolean isVersioned(WType selectedType) {
        WType versionedType = getTypeByName("Versioned");

        if (versionedType == null) {
            return false;
        }

        if (selectedType.getId().equals(versionedType.getId())) {
            return true;
        }


        return selectedType.inherits(versionedType, types);
    }

    private WType getTypeByName(String name) {
        WType artifact = null;
        for (WType type : types.values()) {
            if (name.equals(type.getName())) {
                artifact = type;
            }
        }
        return artifact;
    }

    private void setupTableBottom(boolean showAdd) {
        table.setWidget(table.getRowCount(), 2, newSpacer());

        ToolBar buttonBar = new ButtonBar();
        // Button actions
        SelectionListener btnListener = new SelectionListener<ComponentEvent>() {
            public void componentSelected(ComponentEvent ce) {
                Button btn = (Button) ce.getComponent();
                if (btn == addButton) {
                    add();
                } else if (btn == cancelButton) {
                    cancel();
                }
            }
        };

        if (showAdd) {
            addButton = new Button("Add", btnListener);
            buttonBar.add(addButton);
        }
        cancelButton = new Button("Cancel", btnListener);
        buttonBar.add(cancelButton);

        table.setWidget(table.getRowCount(), 2, buttonBar);
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
            errorPanel.setMessage(msg);
            resetFormFields();
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
            String name = nameBox.getValue().trim();
            if (name != null || !"".equals(name)) {
                nameBox.setValue(name);
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

        String name = nameBox.getValue();
        String parent = parentSB.getText();
        Map<String, Serializable> properties = getProperties(renderers);

        if (isUserSubmittingVersion()) {
            galaxy.getRegistryService().addVersionedItem(parent,
                    name,
                    versionNameBox.getValue(),
                    null,
                    getType().getId(),
                    getVersionType().getId(),
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
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        for (String p : typeRenderers.keySet()) {
            AbstractPropertyRenderer r = typeRenderers.get(p);
            if (r instanceof ArtifactRenderer) {
                properties.put(p, fileId);
            } else {
                properties.put(p, (Serializable) r.getValueToSave());
            }
        }
        return properties;
    }

    private AbstractCallback geAddItemCallback() {
        AbstractCallback callback = new AbstractCallback(errorPanel) {
            public void onSuccess(Object id) {
                History.newItem("item/" + id);
            }

            @Override
            public void onFailure(Throwable caught) {
                resetFormFields();

                if (caught instanceof ItemExistsException) {
                    errorPanel.setMessage("An item with that name already exists.");
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
        galaxy.getPageManager().createPageInfo(token, failurePanel, 0);
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
        errorPanel.clearErrorMessage();
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
     *
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
