package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SimpleRenderer extends AbstractPropertyRenderer {

    private TextBox valueTB;
    private Label valueLabel;

    public Widget createEditForm() {
        valueTB = new TextBox();
        valueTB.setVisibleLength(50);
        valueTB.setText((String) value);
        return valueTB;
    }

    public Object getValueToSave() {
        return valueTB.getText();
    }

    protected String getRenderedText() {
        String txt = (String) value;
        
        if ("".equals(txt) || txt == null) {
            txt = "-----";
        }
        
        return txt;
    }
    
    public Widget createViewWidget() {
        valueLabel = new Label();
        valueLabel.setText(getRenderedText());
        return valueLabel;
    }
    
}
