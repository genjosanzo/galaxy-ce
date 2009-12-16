package org.mule.galaxy.repository.client.property;

import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MapRenderer extends AbstractPropertyRenderer {
    private Map<ValidatableTextBox, ValidatableTextBox> boxes = new HashMap<ValidatableTextBox, ValidatableTextBox>();
    
    public Widget createEditForm() {
        boxes.clear();
        final FlowPanel panel = new FlowPanel();
        
        Map<String,String> map = (Map<String,String>) value;
        if (map != null) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                InlineFlowPanel entryPanel = createKeyValueEditor(panel, e.getKey(), e.getValue(), false);
                
                panel.add(entryPanel);
            }
        }
        
        InlineFlowPanel entryPanel = createKeyValueEditor(panel, "", "", true);
        panel.add(entryPanel);
        
        return panel;
    }

    /**
     * Creates a panel which allows you to edit a specific key and value.
     * @param add TODO
     */
    private InlineFlowPanel createKeyValueEditor(final FlowPanel panel, String key, String value, boolean add) {
        final InlineFlowPanel entryPanel = new InlineFlowPanel();
        
        final ValidatableTextBox keyBox = new ValidatableTextBox(new StringNotEmptyValidator());
        keyBox.setText(key);
        keyBox.getTextBox().setVisibleLength(15);
        
        final ValidatableTextBox valueBox = new ValidatableTextBox(new StringNotEmptyValidator());
        valueBox.setText(value);
        valueBox.getTextBox().setVisibleLength(45);
        
        entryPanel.add(keyBox);
        entryPanel.add(new Label(" : "));
        entryPanel.add(valueBox);
        
        if (add) {
            Button addBtn = new Button("Add");
            addBtn.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    panel.insert(createKeyValueEditor(panel, keyBox.getText(), valueBox.getText(), false), 
                                 panel.getWidgetCount()-1);

                    keyBox.setText("");
                    valueBox.setText("");
                }
            });
            entryPanel.add(addBtn);
        } else {
            Image removeImg = new Image("images/delete_config.gif");
            removeImg.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    panel.remove(entryPanel);
                    boxes.remove(keyBox);
                }
            });
            entryPanel.add(removeImg);
            
            boxes.put(keyBox, valueBox);
            
        }
        return entryPanel;
    }

    public Object getValueToSave() {
        Map<String,String> map = new HashMap<String, String>();
        value = map;
        
        for (Map.Entry<ValidatableTextBox, ValidatableTextBox> e : boxes.entrySet()) {
            map.put(e.getKey().getText(), e.getValue().getText());
        }
        
        return value;
    }

    public Widget createViewWidget() {
        if (!(value instanceof Map)) return new Label("");
        
        Map<String,String> map = (Map<String,String>) value;
        
        if (map == null || map.size() == 0) {
            return new Label("-----");
        }
        
        FlexTable table = new FlexTable();
        int row = 0;
        for (Map.Entry<String, String> e : map.entrySet()) {
            table.setText(row, 0, e.getKey() + ":");
            table.setWidget(row, 1, SimpleRenderer.createWidget(e.getValue()));
            row++;
        }
        
        return table;
    }

    @Override
    public boolean validate() {
        boolean validated = true;
        for (Map.Entry<ValidatableTextBox, ValidatableTextBox> e : boxes.entrySet()) {
            validated &= e.getKey().validate();
            validated &= e.getValue().validate();
        }
        return validated;
    }
    
}
