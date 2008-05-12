package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WGroup;

public class GroupForm extends AbstractAdministrationForm {

    private WGroup group;
    private TextBox nameTB;
    
    public GroupForm(AdministrationPanel adminPanel) {
        super(adminPanel, "groups", "Group was saved.", "Group was deleted.");
    }

    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Name:");
        
        nameTB = new TextBox();
        nameTB.setText(group.getName());
        table.setWidget(0, 1, nameTB);
        
        styleHeaderColumn(table);
    }

    protected void fetchItem(String id) {
        getSecurityService().getGroup(group.getId(), getFetchCallback());
    }

    public String getTitle() {
        if (newItem) {
            return "Add Group";
        } else {
            return "Edit Group: " + group.getName();
        }
    }

    protected void initializeItem(Object o) {
        group = (WGroup) o;
    }

    protected void initializeNewItem() {
        group = new WGroup();
    }

    protected void save() {
        super.save();
        
        group.setName(nameTB.getText());
        getSecurityService().save(group, getSaveCallback());
    }

    protected void delete() {
        super.delete();
        
        getSecurityService().deleteGroup(group.getId(), getDeleteCallback());
    }
}
