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

/**
 * Encapsulates the rendering and editing of a property value.
 */
public abstract class PropertyPanel extends AbstractComposite {

    private SimplePanel panel;
    private InlineFlowPanel viewPanel;
    private InlineFlowPanel editPanel;
    private Button save;
    private Button cancel;
    protected ErrorPanel errorPanel;
    private String itemId;
    private WProperty property;
    private Hyperlink editHL;
    private Hyperlink deleteHL;
    protected Galaxy galaxy;
    private ClickListener deleteListener;
    private ClickListener saveListener;
    private ClickListener cancelListener;

    public PropertyPanel() {
        super();
        
        this.panel = new SimplePanel();

        initWidget(panel);
    }
    
    public void initialize() {
        editHL = new Hyperlink("Edit", "edit-property");
        editHL.setStyleName("propertyLink");
        editHL.addClickListener(new ClickListener() {

            public void onClick(Widget widget) {
                showEdit();
             }
            
        });
        
        deleteHL = new Hyperlink("Delete", "delete-property");
        deleteHL.setStyleName("propertyLink");
        deleteHL.addClickListener(new ClickListener() {

            public void onClick(Widget widget) {
               delete();
            }
            
        });
        
        viewPanel = new InlineFlowPanel();
        viewPanel.add(createViewWidget());
        
        if (!property.isLocked()) {
            viewPanel.add(editHL);
            viewPanel.add(deleteHL);
        }
        
        editPanel = new InlineFlowPanel();
        editPanel.add(createEditForm());
        
        cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                showView();
            }
            
        });
        
        if (cancelListener != null) {
            cancel.addClickListener(cancelListener);
        }
        
        save = new Button("Save");
        save.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                cancel.setEnabled(false);
                save.setEnabled(false);
                
                save();
            }
            
        });
        
        editPanel.add(cancel);
        editPanel.add(save);
    }
    
    protected abstract Widget createViewWidget();

    protected abstract Widget createEditForm();
    
    public void showView() {
        panel.clear();
        panel.add(viewPanel);
    }
    
    protected void delete() {
        galaxy.getRegistryService().deleteProperty(itemId, property.getName(), new AbstractCallback(errorPanel) {

            public void onSuccess(Object arg0) {
                deleteListener.onClick(deleteHL);
            }
            
        });
    }
    public void showEdit() {
        panel.clear();
        panel.add(editPanel);
    }

    public boolean saveAsCollection() {
        return property.isMultiValued();
    }
    
    protected void save() {
        final Object value = getValueToSave();
        
        AbstractCallback saveCallback = new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                
                onSaveFailure(caught, this);
            }

            public void onSuccess(Object arg0) {
                setEnabled(true);
                property.setValue(value);
                onSave(value);
                
                showView();
                
                if (saveListener != null) {
                    saveListener.onClick(save);
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

    protected abstract void onSave(final Object value);
    
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

    private void setEnabled(boolean b) {
        cancel.setEnabled(b);
        save.setEnabled(b);
    }

    public void setDeleteListener(ClickListener deleteListener) {
        this.deleteListener = deleteListener;
        
    }

    public void setSaveListener(ClickListener saveListener) {
        this.saveListener = saveListener;
    }

    public void setCancelListener(ClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }


}
