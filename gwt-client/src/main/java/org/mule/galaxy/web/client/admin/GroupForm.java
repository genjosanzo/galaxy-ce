package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WGroup;

public class GroupForm extends AbstractFlowComposite {

    private AbstractMenuPanel adminPanel;
    private WGroup group;
    private Button save;
    private TextBox nameTB;
    
    public GroupForm(AbstractMenuPanel adminPanel, WGroup u) {
        this (adminPanel, u, false);
    }
    
    public GroupForm(AbstractMenuPanel adminPanel) {
        this (adminPanel, new WGroup(), true);
    }
    
    protected GroupForm(AbstractMenuPanel adminPanel, WGroup grp, boolean add){
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
        adminPanel.getSecurityService().save(group, new AbstractCallback(adminPanel) {

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
