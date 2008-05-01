package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

public abstract class AbstractForm extends AbstractComposite {

    protected FlowPanel panel;
    protected boolean newItem;
    private Button save;
    private Button delete;
    private String successToken;
    private final String successMessage;
    private final ErrorPanel errorPanel;
    private final String deleteMessage;
    
    public AbstractForm(ErrorPanel errorPanel, boolean newItem, String successToken, 
                        String successMessage, String deleteMessage) {
        super();
        this.errorPanel = errorPanel;
        this.newItem = newItem;
        this.successToken = successToken;
        this.successMessage = successMessage;
        this.deleteMessage = deleteMessage;
        panel = new FlowPanel();
        
        initWidget(panel);
    }

    public void onShow() {
        panel.clear();

        panel.add(createTitle(getTitle()));
        
        save = new Button("Save");
        save.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                save();
            }
        });
        
        delete = new Button("Delete");
        delete.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                delete();
            }
        });
        
        FlexTable table = new FlexTable();
        
        addFields(table);
        
        table.setCellSpacing(5);
        table.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        table.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        
        panel.add(table);
        
        if (newItem) {
            panel.add(save);
        } else {
            panel.add(asHorizontal(save, delete));
        }
    }


    protected abstract void addFields(FlexTable table);

    protected void delete() {
        setEnabled(false);
        save.setText("Deleting...");
    }

    protected void setEnabled(boolean enabled) {
        save.setEnabled(enabled);
        delete.setEnabled(enabled);
        
        if (enabled) {
            save.setText("Save");
            delete.setText("Delete");
        }
    }

    protected void save() {
        setEnabled(false);
        save.setText("Saving...");
    }

    public abstract String getTitle();

    protected AsyncCallback getSaveCallback() {
        return new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                setEnabled(true);
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                setEnabled(false);
                History.newItem(successToken);
                errorPanel.setMessage(successMessage);
            }
            
        };
    }
    
    protected AsyncCallback getDeleteCallback() {
        return new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                setEnabled(true);
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                setEnabled(false);
                History.newItem(successToken);
                errorPanel.setMessage(deleteMessage);
            }
            
        };
    }
}
