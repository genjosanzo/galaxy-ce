package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WUser;

public class UserListPanel
    extends AbstractAdministrationComposite
{
    public UserListPanel(AdministrationPanel a) {
        super(a);
    }
    
    public void onShow() {
        super.onShow();
        
        final FlexTable table = createTitledRowTable(panel, "Users");
        
        table.setText(0, 0, "Username");
        table.setText(0, 1, "Name");
        table.setText(0, 2, "Email");
        
        adminPanel.getSecurityService().getUsers(new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection users = (Collection) result;
                
                int i = 1;
                for (Iterator itr = users.iterator(); itr.hasNext();) {
                    final WUser u = (WUser) itr.next();
                    
                    Hyperlink hyperlink = new Hyperlink(u.getUsername(), 
                                                        "users/" + u.getId());
                    
                    table.setWidget(i, 0, hyperlink);
                    table.setText(i, 1, u.getName());
                    table.setText(i, 2, u.getEmail());
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
        });
    }

    public String getTitle()
    {
        return "Users";
    }
}
