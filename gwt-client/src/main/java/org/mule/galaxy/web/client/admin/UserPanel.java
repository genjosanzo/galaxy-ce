package org.mule.galaxy.web.client.admin;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UserPanel extends Composite {

    private AdministrationPanel adminPanel;

    public UserPanel(AdministrationPanel adminPanel, 
                     WUser u) {
        this.adminPanel = adminPanel;
        
        adminPanel.setTitle("Edit User");
        
        final FlexTable table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        table.setText(0, 0, "Username");
        table.setText(1, 0, "Name");
        table.setText(2, 0, "Email");
        table.setText(3, 0, "Old Password");
        table.setText(4, 0, "Password");
        table.setText(5, 0, "Confirm Password");
        
        table.setText(0, 1, u.getUsername());
        
        TextBox nameTB = new TextBox();
        nameTB.setText(u.getName());
        table.setWidget(1, 1, nameTB);
        
        TextBox emailTB = new TextBox();
        emailTB.setText(u.getEmail());
        table.setWidget(2, 1, emailTB);

        PasswordTextBox oldPass = new PasswordTextBox();
        table.setWidget(3, 1, oldPass);
        
        PasswordTextBox pass = new PasswordTextBox();
        table.setWidget(4, 1, pass);
        
        PasswordTextBox confirm = new PasswordTextBox();
        table.setWidget(5, 1, confirm);
        
        Button submit = new Button("Save");
        table.setWidget(6, 1, submit);
        
        table.getRowFormatter().setStyleName(0, "gwt-FlexTable-header");
                
        initWidget(table);
    }

}
