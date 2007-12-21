package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractCallback;

public class UserListPanel
    extends Composite
{
    private AdministrationPanel adminPanel;

    public UserListPanel(AdministrationPanel panel) {
        super();
        
        this.adminPanel = panel;
        
        final FlexTable table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        table.setText(0, 0, "Username");
        table.setText(0, 1, "Name");
        table.setText(0, 2, "Email");
        table.getRowFormatter().setStyleName(0, "gwt-FlexTable-header");
        
        panel.getUserService().getUsers(new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection users = (Collection) result;
                
                int i = 1;
                for (Iterator itr = users.iterator(); itr.hasNext();) {
                    final WUser u = (WUser) itr.next();
                    
                    Hyperlink hyperlink = new Hyperlink(u.getUsername(), 
                                                        "user-" + u.getUsername());
                    hyperlink.addClickListener(new ClickListener() {
                        public void onClick(Widget sender) {
                            adminPanel.setMain(new UserPanel(adminPanel, u));
                        }
                    });
                    
                    table.setWidget(i, 0, hyperlink);
                    table.setText(i, 1, u.getName());
                    table.setText(i, 2, u.getEmail());
                    i++;
                }
            }
            
        });

        
        initWidget(table);
    }

    public String getTitle()
    {
        return "Users";
    }
}
