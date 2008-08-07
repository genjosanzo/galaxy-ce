package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.LightBox;
import org.mule.galaxy.web.rpc.AbstractCallback;

/**
 * Encapsulates the rendering and editing of a property value.
 */
public abstract class AbstractEditPropertyPanel extends PropertyPanel {

    private InlineFlowPanel viewPanel;
    private FlowPanel editPanel;
    private Button save;
    private Button cancel;
    private Hyperlink editHL;
    private Hyperlink deleteHL;

    public AbstractEditPropertyPanel() {
        super();
    }

    public void initialize() {
        editHL = new Hyperlink("Edit", galaxy.getCurrentToken());
        editHL.setStyleName("propertyLink");
        editHL.addClickListener(new ClickListener() {

            public void onClick(Widget widget) {
                showEdit();
             }
            
        });
        
        deleteHL = new Hyperlink("Delete", galaxy.getCurrentToken());
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
        
        editPanel = new FlowPanel();
        editPanel.setStyleName("add-property-inline");
        Widget editForm = createEditForm();
        editForm.setStyleName("add-property-inline");
        editPanel.add(editForm);
        
        FlowPanel buttonPanel = new FlowPanel();
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
        
        buttonPanel.add(cancel);
        buttonPanel.add(save);
        
        editPanel.add(buttonPanel);
    }
    
    protected abstract Widget createViewWidget();

    protected abstract Widget createEditForm();
    
    public void showView() {
        panel.clear();
        panel.add(viewPanel);
    }
    
    protected void delete() {
        final ConfirmDialog dialog = new ConfirmDialog(new ConfirmDialogAdapter() {
            public void onConfirm() {
                doDelete();
            }
        }, "Are you sure you want to delete this property?");
        
        new LightBox(dialog).show();
    }
    protected void doDelete() {
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
 
    public void setErrorPanel(ErrorPanel errorPanel) {
        this.errorPanel = errorPanel;
    }

    public void setItemId(String entryid) {
        this.itemId = entryid;
    }

    public void setEnabled(boolean b) {
        cancel.setEnabled(b);
        save.setEnabled(b);
    }


}
