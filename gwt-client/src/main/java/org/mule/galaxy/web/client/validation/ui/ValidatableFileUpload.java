package org.mule.galaxy.web.client.validation.ui;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.validation.Validator;

public class ValidatableFileUpload extends AbstractValidatableInputField {

    protected FileUpload fileUpload;

    public ValidatableFileUpload(final Validator validator) {
        super(validator);
    }

    protected Widget createInputWidget() {
        fileUpload = new FileUpload();
        return fileUpload;
    }

    public boolean validate() {
        return getValidator().validate(fileUpload);
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }


}
