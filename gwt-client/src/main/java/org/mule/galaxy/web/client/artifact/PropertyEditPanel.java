package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;
import java.util.Map;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;

public class PropertyEditPanel extends Composite {

    private ListBox propertiesBox;
    private TextBox valueTextBox;
    private FlowPanel propertyPanel;
    private FlexTable newPropertyTable;
    private ErrorPanel errorPanel;
    private String artifactId;
    private ArtifactMetadataPanel metadataPanel;
    private RegistryServiceAsync svc;
    private TextBox idTextBox;
    private TextBox descTextBox;
    private CheckBox mvCheckBox;
    private Panel propertiesPanel;

    public PropertyEditPanel(final ErrorPanel registryPanel, 
                             final RegistryServiceAsync registryService,
                             final String artifactId,
                             final Panel propertiesPanel,
                             final ArtifactMetadataPanel metadataPanel,
                             final FlexTable table) {
        this.errorPanel = registryPanel;
        this.artifactId = artifactId;
        this.propertiesPanel = propertiesPanel; 
        this.metadataPanel = metadataPanel;
        this.svc = registryService;
        
        HorizontalPanel panel = new HorizontalPanel();
        panel.setStyleName("add-property-panel");
        panel.add(new Label("Add Property: "));
        
        propertyPanel = new FlowPanel();
        propertiesBox = new ListBox();
        propertiesBox.setMultipleSelect(false);
        propertiesBox.addItem("", "");
        propertiesBox.addItem("New...", "new");
        
        propertiesBox.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                onPropertySelect((ListBox)w);
            }
            
        });
        propertyPanel.add(propertiesBox);
        
        // Create a wrapper so the propertyPanel gets formatted correctly
        SimplePanel propPanelContainer = new SimplePanel();
        propPanelContainer.add(propertyPanel);
        panel.add(propPanelContainer);
        
        svc.getProperties(new AbstractCallback(registryPanel) {
            public void onSuccess(Object o) {
                initProperties((Map) o);
            }
        });
        
        valueTextBox = new TextBox();
        valueTextBox.setVisibleLength(40);
        panel.add(valueTextBox);
        
        final PropertyEditPanel editPanel = this;
        Button saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                saveProperty();
            }
        });
        panel.add(saveButton);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                propertiesPanel.remove(editPanel);
                newPropertyTable = null;
            }
        });
        panel.add(cancelButton);
        
        initWidget(panel);
    }

    protected void saveProperty() {
        String propertyName = null;
        String propertyDesc = null;
        
        int index = propertiesBox.getSelectedIndex();
        if (index > -1) {
            propertyName = propertiesBox.getValue(index);
            
            if (propertyName.equals("new")) {
                createPropertyAndSave();
                return;
            } 
            
            propertyDesc = propertiesBox.getItemText(index);
        } else {
            errorPanel.setMessage("No property was selected!");
            return;
        }
        
        saveProperty(propertyName, propertyDesc);
    }

    private void saveProperty(final String propertyName, final String propertyDesc) {
        final String propertyValue = valueTextBox.getText();
        
        svc.setProperty(artifactId, propertyName, propertyValue, new AbstractCallback(errorPanel) {

            public void onSuccess(Object o) {
                clearPanelAndAddProperty(propertyName, propertyDesc, propertyValue);
            }
            
        });
    }

    protected void clearPanelAndAddProperty(String propertyName, String propertyDesc, String propertyValue) {
        propertiesPanel.remove(this);
        
        metadataPanel.addProperty(propertyName, propertyDesc, valueTextBox.getText());
    }

    private void createPropertyAndSave() {
        final String id = idTextBox.getText();
        if (id == null || "".equals(id)) {
            errorPanel.setMessage("A property id must be supplied;");
            return;
        }
        final String desc = descTextBox.getText();
        if (desc == null || "".equals(desc)) {
            errorPanel.setMessage("A property description must be supplied;");
            return;
        }
        boolean mv = mvCheckBox.isChecked();
        
        svc.newPropertyDescriptor(id, desc, mv, new AbstractCallback(errorPanel) {

            public void onSuccess(Object arg0) {
                saveProperty(id, desc);
            }
            
        });
    }

    protected void onPropertySelect(ListBox w) {
        int i = w.getSelectedIndex();
        String txt = w.getValue(i);
        if ("new".equals(txt)) {
            
            newPropertyTable = new FlexTable();
            newPropertyTable.setWidget(0, 0, new Label("Id:"));
            
            idTextBox = new TextBox();
            idTextBox.setVisibleLength(20);
            newPropertyTable.setWidget(0, 1, idTextBox);
            
            newPropertyTable.setText(1, 0, "Description:");
            
            descTextBox = new TextBox();
            descTextBox.setVisibleLength(20);
            newPropertyTable.setWidget(1, 1, descTextBox);
            
            newPropertyTable.setText(2, 0, "Multivalued:");
            
            mvCheckBox = new CheckBox();
            newPropertyTable.setWidget(2, 1, mvCheckBox);
            
            propertyPanel.add(newPropertyTable);
        } else {
            for (int c = 1; c < propertyPanel.getWidgetCount(); c++) {
                propertyPanel.remove(1);
            }
        }
    }

    protected void initProperties(Map o) {
        for (Iterator itr = o.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry) itr.next();
            propertiesBox.addItem((String)e.getValue(), (String)e.getKey());
        }
    }

}
