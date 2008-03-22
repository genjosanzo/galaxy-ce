package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.util.QNameListBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WIndex;

public class GroupForm extends AbstractFlowComposite {

    private AdministrationPanel adminPanel;
    private WGroup group;
    private Button save;
    private TextBox nameTB;
    
    public GroupForm(AdministrationPanel adminPanel, WGroup u) {
        this (adminPanel, u, false);
    }
    
    public GroupForm(AdministrationPanel adminPanel) {
        this (adminPanel, new WGroup(), true);
    }
    
    protected GroupForm(AdministrationPanel adminPanel, WGroup grp, boolean add){
        super();
        this.adminPanel = adminPanel;
        this.group = grp;
        
        String title;
        if (add) {
            title = "Add Group";
        } else {
            title = "Edit Group: " + grp.getName();
        }
        
        final FlexTable table = createTitledColumnTable(panel, title);
        
        table.setText(0, 0, "Name");
        
        nameTB = new TextBox();
        nameTB.setText(grp.getName());
        table.setWidget(0, 1, nameTB);
        
        save = new Button("Save");
        table.setWidget(1, 1, save);
        save.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                save();
            }
            
        });
        
        styleHeaderColumn(table);
    }


    protected void save() {
        save.setEnabled(false);
        save.setText("Saving...");
        group.setName(nameTB.getText());
        adminPanel.getUserService().save(group, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                
                reenable();
            }

            public void onSuccess(Object arg0) {
                History.newItem("groups");
            }
        });
    }

    private void reenable() {
        save.setEnabled(true);
        save.setText("Save");
    }

}
