package org.mule.galaxy.web.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.UserService;
import org.mule.galaxy.web.rpc.UserServiceAsync;

public class AdministrationPanel extends AbstractMenuPanel {

    private UserServiceAsync userService;
    private Hyperlink add;
    private RegistryServiceAsync registryService;

    public AdministrationPanel(Galaxy galaxy) {
        super(galaxy);
        this.registryService = galaxy.getRegistryService();
        this.userService = galaxy.getUserService();

        Toolbox manageBox = new Toolbox(false);
        manageBox.setTitle("Manage");
        addMenuItem(manageBox);

        final AdministrationPanel adminPanel = this;

        Hyperlink link = new Hyperlink("Lifecycles", "lifecycles");
        manageBox.add(link);
        createPageInfo(link.getTargetHistoryToken(), new LifecyclePanel(adminPanel));

        link = new Hyperlink("Indexes", "indexes");
        manageBox.add(link);
        createPageInfo(link.getTargetHistoryToken(), new IndexListPanel(adminPanel));

        add = new Hyperlink("Add", "add-index");
        createPageInfo(add.getTargetHistoryToken(), new IndexForm(adminPanel));

        InlineFlowPanel item = new InlineFlowPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));

        manageBox.add(item);

        link = new Hyperlink("Policies", "policies");
        createPageInfo(link.getTargetHistoryToken(), new PolicyPanel(adminPanel, registryService));
        manageBox.add(link);

        link = new Hyperlink("Users", "users");
        createPageInfo(link.getTargetHistoryToken(), new UserListPanel(adminPanel));
        
        add = new Hyperlink("Add", "add-user-form");
        createPageInfo(add.getTargetHistoryToken(), new UserForm(adminPanel));

        item = new InlineFlowPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));

        manageBox.add(item);
    }

    public void showUsers() {
        History.newItem("users");
    }

    public UserServiceAsync getUserService() {
        return userService;
    }

    public RegistryServiceAsync getRegistryService() {
        return registryService;
    }
}
