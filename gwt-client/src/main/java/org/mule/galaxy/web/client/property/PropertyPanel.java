package org.mule.galaxy.web.client.property;

import java.util.Collection;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WProperty;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

/**
 * Encapsulates the rendering and editing of a property value.
 */
public abstract class PropertyPanel extends AbstractComposite {

    protected InlineFlowPanel panel;
    protected ErrorPanel errorPanel;
    protected String itemId;
    protected WProperty property;
    protected Galaxy galaxy;
    protected ClickListener saveListener;
    protected ClickListener deleteListener;
    protected ClickListener cancelListener;

    public PropertyPanel() {
        super();
        
        this.panel = new InlineFlowPanel();

        initWidget(panel);
    }
    
    public void initialize() {
    }
    
    public boolean saveAsCollection() {
        return property.isMultiValued();
    }
    
    public abstract void showView();
    
    protected void save() {
        final Object value = getValueToSave();
        
        AbstractCallback saveCallback = new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                
                onSaveFailure(caught, this);
            }

            public void onSuccess(Object response) {
                setEnabled(true);
                property.setValue(value);
                onSave(value, response);
                
                showView();

                if (saveListener != null) {
                    saveListener.onClick(null);
                }
            }
            
        };
        
        setEnabled(false);
        if (saveAsCollection()) {
            galaxy.getRegistryService().setProperty(itemId, 
                                                    property.getName(), 
                                                    (Collection) value, 
                                                    saveCallback);
            
        } else {
            galaxy.getRegistryService().setProperty(itemId, 
                                                    property.getName(), 
                                                    (String) value, 
                                                    saveCallback);
        }
    }

    protected abstract void onSave(final Object value, Object response);
    
    protected void onSaveFailure(Throwable caught, AbstractCallback saveCallback) {
        saveCallback.onFailureDirect(caught);
    }

    /**
     * The value that should be sent to the RegistryService.
     * @return
     */
    protected abstract Object getValueToSave();
    
    public WProperty getProperty() {
        return property;
    }

    public void setProperty(WProperty property) {
        this.property = property;
    }
    
    public void setGalaxy(Galaxy galaxy) {
        this.galaxy = galaxy;
    }

    public void setErrorPanel(ErrorPanel errorPanel) {
        this.errorPanel = errorPanel;
    }

    public void setItemId(String entryid) {
        this.itemId = entryid;
    }

    protected void setEnabled(boolean b) {
    }

    public void setSaveListener(ClickListener saveListener) {
        this.saveListener = saveListener;
    }

    public void setDeleteListener(ClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }
    public void setCancelListener(ClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public abstract void showEdit();
}
