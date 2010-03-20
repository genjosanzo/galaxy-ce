package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.Widget;

import java.io.Serializable;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.rpc.AbstractCallback;

public abstract class AbstractPropertyRenderer {
    protected Galaxy galaxy;
    protected ErrorPanel errorPanel;
    protected Object value;
    protected boolean bulkEdit;
    protected boolean editSupported = true;
    
    public void initialize(Galaxy galaxy, ErrorPanel errorPanel, Object value, boolean bulkEdit) {
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.value = value;             
        this.bulkEdit = bulkEdit;
    }
    
    public abstract Widget createEditForm();
    
    public abstract Widget createViewWidget();
    
    public abstract Object getValueToSave();
    
    public abstract boolean validate();
 
    public void save(String itemId, String name, Serializable valueToSave, AbstractCallback saveCallback) {
        galaxy.getRegistryService().setProperty(itemId, 
                                                name, 
                                                valueToSave, 
                                                saveCallback);
    }

    public boolean isEditSupported() {
        return editSupported;
    }
    
}