package org.mule.galaxy.repository.client.property;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.ui.field.ValidatableWidget;
import org.mule.galaxy.web.client.ui.panel.InlineFlowPanel;
import org.mule.galaxy.web.client.ui.field.Validator;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WUser;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class UserListRenderer extends AbstractListRenderer {

    private Collection users;
    private ListBox userLB;
    private Button addButton;
    private ValidatableWidget userValidator;
    
    protected void loadRemote() {
        userLB = new ListBox();
        userLB.addItem("Loading...");
        
        if (users == null) { 
            repositoryModule.getGalaxy().getSecurityService().getUsers(new AbstractCallback(errorPanel) {
    
                public void onSuccess(Object o) {
                    users = (Collection) o;
                    onFinishLoad();
                }
                
            });
        } else {
            onFinishLoad();
        }
        

        Validator validator = new Validator() {
            public String getFailureMessage() {
                return "At least one user must be supplied.";
            }

            public boolean validate(Object value) {
                return values.size() > 0;
            }
        };
        
        userValidator = new ValidatableWidget(userLB, validator);
    }
    
    protected void removeLabel(Object id) {
        super.removeLabel(id);

        updateUsers();
    }

    @Override
    protected void redrawEditPanel() {
        super.redrawEditPanel();
        
        updateUsers();
    }

    private void updateUsers() {
        userLB.clear();
        for (Iterator itr = users.iterator(); itr.hasNext();) {
            WUser user = (WUser) itr.next();
            
            if (!values.contains(user.getId())) {
                userLB.addItem(user.getName(), user.getId());
            }
        }

        addButton.setEnabled(true);
    }

    protected void addUserLabelForSelection() {
        int idx = userLB.getSelectedIndex();

        if (idx == -1) {
            return;
        }
        String id = userLB.getValue(idx);
        
        values.add(id);

        editValuesPanel.add(createLabel(id));
        userLB.removeItem(idx);

        userValidator.clearError();
    }

    @Override
    protected String getRenderedText(Object id) {
        WUser user = getUser((String) id);
        
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
        InlineFlowPanel addPanel = new InlineFlowPanel();
        addPanel.setStyleName("renderer-add-panel");
        addPanel.add(userValidator);
        
        addButton = new Button();
        addButton.setText("Add");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                addUserLabelForSelection();
            }
        });
        addButton.setEnabled(false);

        addPanel.add(addButton);
        return addPanel;
    }

    @Override
    public boolean validate() {
        return userValidator.validate();
    }

}
