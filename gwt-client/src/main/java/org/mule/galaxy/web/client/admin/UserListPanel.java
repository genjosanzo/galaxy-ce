package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

public class UserListPanel
    extends Composite
{
    public UserListPanel(UserServiceAsync userService) {
        super();
        
        final FlexTable table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        table.setText(0, 0, "Username");
        table.setText(0, 1, "Name");
        table.setText(0, 2, "");
        table.getRowFormatter().setStyleName(0, "gwt-FlexTable-header");
        
        userService.getUsers(new AsyncCallback() {

            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                
            }

            public void onSuccess(Object result) {
                Collection users = (Collection) result;
                
                for (Iterator itr = users.iterator(); itr.hasNext();) {
                    WUser u = (WUser) itr.next();
                    
                    table.setText(1, 0, u.getUsername());
                    table.setText(1, 1, u.getName());
                    table.setHTML(1, 2, new Hyperlink("Edit", "user-" + u.getUsername()).getHTML());
                }
            }
            
        });

        
        initWidget(table);
    }

    public String getTitle()
    {
        return "WSDL";
    }
}
