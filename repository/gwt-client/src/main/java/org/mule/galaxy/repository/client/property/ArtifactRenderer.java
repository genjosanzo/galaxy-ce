package org.mule.galaxy.repository.client.property;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;

import org.mule.galaxy.web.client.ui.ExternalHyperlink;
import org.mule.galaxy.web.client.ui.field.ValidatableTextBox;

public class ArtifactRenderer extends AbstractPropertyRenderer {

    private ValidatableTextBox valueTB;
    private FileUpload upload;

    public ArtifactRenderer() {
        super();
        editSupported = false;
    }

    public Widget createEditForm() {
        upload = new FileUpload();
        upload.setName("file");
        return upload;
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
    
    public static Widget createWidget(String txt, String id) {
        return new ExternalHyperlink("View", txt, id);
    }

    @Override
    public boolean validate() {
        return upload.getFilename() != null;
    }
}
