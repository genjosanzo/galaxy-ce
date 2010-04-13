package org.mule.galaxy.repository.client.property;

import java.io.Serializable;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractPropertyRenderer {
    protected RepositoryModule repositoryModule;
    protected ErrorPanel errorPanel;
    protected Object value;
    protected boolean bulkEdit;
    protected boolean editSupported = true;
    protected RegistryServiceAsync registryService;
    
    public void initialize(RepositoryModule repositoryModule, ErrorPanel errorPanel, Object value, boolean bulkEdit) {
        this.repositoryModule = repositoryModule;
        this.registryService = repositoryModule.getRegistryService();
        this.errorPanel = errorPanel;
        this.value = value;             
        this.bulkEdit = bulkEdit;
    }
    
    public abstract Widget createEditForm();
    
    public abstract Widget createViewWidget();
    
    public abstract Object getValueToSave();
    
    public abstract boolean validate();
 
    public void save(String itemId, String name, Serializable valueToSave, AbstractCallback saveCallback) {
        registryService.setProperty(itemId, name, valueToSave, saveCallback);
    }

    public boolean isEditSupported() {
        return editSupported;
    }
    
}