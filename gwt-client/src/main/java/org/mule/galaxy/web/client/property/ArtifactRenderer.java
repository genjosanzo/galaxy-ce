package org.mule.galaxy.web.client.property;

import org.mule.galaxy.web.client.util.ExternalHyperlink;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;

import com.google.gwt.user.client.ui.Widget;

public class ArtifactRenderer extends AbstractPropertyRenderer {

    private ValidatableTextBox valueTB;

    public ArtifactRenderer() {
        super();
        editSupported = false;
    }

    public Widget createEditForm() {
        throw new UnsupportedOperationException();
    }

    public Object getValueToSave() {
        return valueTB.getText();
    }

    public Widget createViewWidget() {
        String txt = (String) value;
        
        return createWidget(txt);
    }

    public static Widget createWidget(String txt) {
        return new ExternalHyperlink("View", txt);
    }

    @Override
    public boolean validate() {
        return valueTB.validate();
    }
    
}
