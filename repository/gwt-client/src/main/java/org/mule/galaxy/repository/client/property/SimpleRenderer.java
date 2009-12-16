package org.mule.galaxy.repository.client.property;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.util.ExternalHyperlink;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;

public class SimpleRenderer extends AbstractPropertyRenderer {

    private ValidatableTextBox valueTB;

    public Widget createEditForm() {
        valueTB = new ValidatableTextBox(new StringNotEmptyValidator());
        valueTB.getTextBox().setVisibleLength(50);
        valueTB.setText((String) value);
        return valueTB;
    }

    public Object getValueToSave() {
        return valueTB.getText();
    }

    public Widget createViewWidget() {
        String txt = value.toString();
        
        return createWidget(txt);
    }

    public static Widget createWidget(String txt) {
        if ("".equals(txt) || txt == null) {
            return new Label("-----");
        }
        
        String[] split = txt.split(" ");
        boolean foundLink = false;
        boolean first = true;
        InlineFlowPanel panel = new InlineFlowPanel();
        
        for (String s : split) {
            if (!first) {
                panel.add(new Label(" "));
            } else {
                first = false;
            }
            
            if (s.matches("\\b(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
                foundLink = true;
                panel.add(new ExternalHyperlink(s, s));
            } else {
                panel.add(new Label(s));
            }
        }
        
        if (foundLink) {
            return panel;
        } else {
            return new Label(txt);
        }
    }

    @Override
    public boolean validate() {
        return valueTB.validate();
    }
    
}
