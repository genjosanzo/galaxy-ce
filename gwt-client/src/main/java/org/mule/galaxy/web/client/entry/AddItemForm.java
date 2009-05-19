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

package org.mule.galaxy.web.client.entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.property.AbstractPropertyRenderer;
import org.mule.galaxy.web.client.property.PropertyInterfaceManager;
import org.mule.galaxy.web.client.registry.PolicyResultsPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.ItemPathOracle;
import org.mule.galaxy.web.client.util.PropertyDescriptorComparator;
import org.mule.galaxy.web.client.util.StringUtil;
import org.mule.galaxy.web.client.util.TooltipListener;
import org.mule.galaxy.web.client.util.WTypeComparator;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableSuggestBox;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.rpc.WType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddItemForm extends AbstractErrorShowingComposite
        implements FormHandler, ClickListener {

    private FlexTable table;
    private FormPanel form;
    private ValidatableTextBox nameBox;
    private ValidatableSuggestBox parentSB;
    private final Galaxy galaxy;
    private String itemId;
    private boolean add;
    private Button addButton;
    private Button cancelButton;
    private ListBox typeChoice;
    protected Map<String,WType> types;
    private PropertyInterfaceManager factory = new PropertyInterfaceManager();
    private ItemInfo item;
    private Map<String, AbstractPropertyRenderer> renderers = new HashMap<String, AbstractPropertyRenderer>();
    
    public AddItemForm(final Galaxy galaxy) {
        this.galaxy = galaxy;

        FlowPanel main = getMainPanel();
        form = new FormPanel();
        main.add(form);
        initWidget(main);
    }

    public void onHide() {
        form.clear();
    }


    public void onShow(List<String> params) {
        if (params.size() > 0) {
            itemId = params.get(0);
            add = false;
            
            galaxy.getRegistryService().getItemInfo(itemId, false, new AbstractCallback<ItemInfo>(this) {
                public void onSuccess(ItemInfo item) {
                    AddItemForm.this.item = item;
                    finishShow();
                }
            });
        } else {
            add = true;
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

        parentSB = new ValidatableSuggestBox(new StringNotEmptyValidator(),
                                             new ItemPathOracle(galaxy, this));
        parentSB.getTextBox().setName("workspacePath");
        if (item != null) {
            parentSB.setText(item.getPath());
        }
        table.setWidget(1, 2, parentSB);

        table.setWidget(2, 0, new Label("Name:"));

        // to control formatting
        final Image spacerimg = new Image("images/clearpixel.gif");
        spacerimg.setWidth("16px");
        table.setWidget(2, 1, spacerimg);

        nameBox = new ValidatableTextBox(new StringNotEmptyValidator());
        nameBox.getTextBox().setName("name");
        table.setWidget(2, 2, nameBox);

        // warn user if the artifact name does not contain an extention.
        nameBox.getTextBox().addFocusListener(new FocusListener() {
            public void onLostFocus(final Widget sender) {
                String s = ((TextBox) sender).getText();
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
            public void onFocus(final Widget sender) {
            } 

        });

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
        
        typeChoice.addChangeListener(new ChangeListener() {
            public void onChange(Widget arg0) {
                WType type = getType();
                if (type != null) {
                    selectType(type);
                }
            }
        });
        
        setupTableBottom(3, false);
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
        
        for (int i = 3; i < table.getRowCount(); i++) 
            table.removeRow(3);
        
        renderers.clear();
        
        int row = 3;
        for (WPropertyDescriptor pd : props) {
            table.setText(row, 0, pd.getDescription());
            AbstractPropertyRenderer renderer = factory.createRenderer(pd.getExtension(), pd.isMultiValued());
            renderer.initialize(galaxy, this, null, false);
            renderers.put(pd.getName(), renderer);
            table.setWidget(row, 2, renderer.createEditForm());
            
            row++;
        }
        
        setupTableBottom(row, true);
    }

    private void setupTableBottom(int row, boolean showAdd) {

        InlineFlowPanel buttons = new InlineFlowPanel();
        if (showAdd) {
            addButton = new Button("Add");
            addButton.addClickListener(this);
            buttons.add(addButton);
        }
        
        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(this);

        buttons.add(cancelButton);

        table.setWidget(row, 2, buttons);

        setTitle("Add Item");
        
        form.addFormHandler(this);
        styleHeaderColumn(table);
    }


    public void onSubmit(final FormSubmitEvent event) {

        
    }


    public void onSubmitComplete(FormSubmitCompleteEvent event) {

        String msg = event.getResults();

        // some platforms insert css info into the pre-tag -- just remove it all
        msg = msg.replaceAll("\\<.*?\\>", "");

        // This is our 200 OK response
        // eg:  OK 9c495a52-4a07-4697-ba73-f94f95cd3020
        if (msg.startsWith("OK ")) {
            String artifactId2 = itemId;
            if (add) {
                // remove the "OK " string to get the artifactId
                artifactId2 = msg.substring(3);
            }
            // send them to the view artifact info page on success.
            History.newItem("artifact/" + artifactId2);
        } else

            // something bad happened...
            if (msg.startsWith("ArtifactPolicyException")) {
                parseAndShowPolicyMessages(msg);
            } else {
                this.setMessage(msg);
                resetFormFields();
            }
    }


    public void onClick(Widget sender) {
        if (sender == addButton) {
//            form.submit();
            
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
            Map<String,Serializable> properties = new HashMap<String, Serializable>();
            for (String p : renderers.keySet()) {
                AbstractPropertyRenderer r = renderers.get(p);
                properties.put(p, (Serializable)r.getValueToSave());
            }
            galaxy.getRegistryService().addItem(parentSB.getText(), 
                                                nameBox.getText(),
                                                null, 
                                                getType().getId(), 
                                                properties, 
                                                callback);
        }

        if (sender == cancelButton) {
            String token = "browse";
            if (item != null) {
                token += "/" + item.getId();
            }
            History.newItem(token);
        }

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

        v &= parentSB.validate();
        v &= nameBox.validate();
        
        for (AbstractPropertyRenderer r : renderers.values()) {
            v &= r.validate();
        }
        return v;
    }


    private void resetFormFields() {
        addButton.setText("Add");
        addButton.setEnabled(true);
    }

    public void setItem(ItemInfo item) {
        this.item = item;
    }

}
