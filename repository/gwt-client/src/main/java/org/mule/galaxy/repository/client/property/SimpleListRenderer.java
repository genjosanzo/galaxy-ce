package org.mule.galaxy.repository.client.property;

import java.util.Collection;

import org.mule.galaxy.web.client.ui.field.StringListBox;
import org.mule.galaxy.web.client.ui.panel.InlineFlowPanel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleListRenderer extends AbstractPropertyRenderer {

    private SimplePanel listBoxPanel;
    private StringListBox listBox;

    @SuppressWarnings("unchecked")
    public Widget createEditForm() {
        listBoxPanel = new SimplePanel();
        listBox = new StringListBox((Collection<String>) value);
        
        listBoxPanel.add(listBox);
        return listBoxPanel;
    }

    public Widget createViewWidget() { 
        Collection<String> c = (Collection<String>) value;
        
        if (c == null || c.size() == 0) {
            return new Label("-----");
        } else {
            boolean first = true;
            InlineFlowPanel panel = new InlineFlowPanel();
            for (String s : c) {
                if (first) {
                    first = false;
                } else {
                    panel.add(new Label(", "));
                }
                
                panel.add(SimpleRenderer.createWidget(s));
            }
            return panel;
        }
    }

    public Object getValueToSave() {
        return listBox.getItems();
    }

    @Override
    public boolean validate() {
        return listBox.validate();
    }

}
