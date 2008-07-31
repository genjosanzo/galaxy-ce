package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WUser;

public class UserListPropertyPanel extends AbstractListPropertyPanel {

    protected Collection users;
    FlowPanel addPanel = new FlowPanel();
    private Button addButton;
    private ListBox userLB;
    
    protected void loadRemote() {
        galaxy.getSecurityService().getUsers(new AbstractCallback(errorPanel) {

            public void onSuccess(Object o) {
                users = (Collection) o;
                onFinishLoad();
            }
            
        });
    }
    
    
    protected void onFinishLoad() {
        userLB = new ListBox();
        for (Iterator itr = users.iterator(); itr.hasNext();) {
            WUser user = (WUser) itr.next();
            
            userLB.addItem(user.getName(), user.getId());
        }
        addPanel.add(userLB);
        
        addButton = new Button();
        addButton.setText("Add");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                addUserLabelForSelection();
            }
        });
        addPanel.add(addButton);
        
        super.onFinishLoad();
    }


    protected void addUserLabelForSelection() {
        int idx = userLB.getSelectedIndex();
        String id = userLB.getValue(idx);
        
        values.add(id);

        editValuesPanel.add(createLabel(id));
    }


    protected String getRenderedText(String id) {
        WUser user = getUser(id);
        
        if (user != null) {
            return user.getName();
        }
        return "";
    }
    
    private WUser getUser(String id) {
        for (Iterator itr = users.iterator(); itr.hasNext();) {
            WUser user = (WUser) itr.next();
            
            if (id.equals(user.getId())) {
                return user;
            }
        }
        return null;
    }
    
    protected Widget getAddWidget() {
        return addPanel;
    }

}
