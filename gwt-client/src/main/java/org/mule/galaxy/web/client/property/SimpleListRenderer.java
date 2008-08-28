package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

import org.mule.galaxy.web.client.util.StringListBox;

public class SimpleListRenderer extends AbstractPropertyRenderer {

    private Label valueLabel;
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
        valueLabel = new Label();
        valueLabel.setText(getRenderedText());
        return valueLabel;
    }

    public Object getValueToSave() {
        return listBox.getItems();
    }

    @SuppressWarnings("unchecked")
    protected String getRenderedText() {
        Collection<String> c = (Collection<String>) value;
        
        String txt;
        if (c == null || c.size() == 0) {
            txt = "-----";
        } else {
            txt = c.toString();
            txt = txt.substring(1, txt.length()-1);
        }
        
        return txt;
    }

}
