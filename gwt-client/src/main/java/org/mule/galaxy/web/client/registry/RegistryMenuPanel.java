package org.mule.galaxy.web.client.registry;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.util.NavigationUtil;
import org.mule.galaxy.web.client.util.Toolbox;

/**
 * Forms the basis of any pages which do not list artifacts.
 */
public class RegistryMenuPanel extends MenuPanel {

    public RegistryMenuPanel() {
        this(true, true);
    }

    public RegistryMenuPanel(boolean showBrowse, boolean showSearch) {
        super();
        
        Image addImg = new Image("images/add_obj.gif");
        addImg.addClickListener(NavigationUtil.createNavigatingClickListener("add-artifact"));
        
        Image addWkspcImg = new Image("images/fldr_obj.gif");
        addWkspcImg.addClickListener(NavigationUtil.createNavigatingClickListener("add-workspace"));

        Toolbox topMenuLinks = new Toolbox(false);
        topMenuLinks.add(asHorizontal(addImg, new Label(" "), new Hyperlink("Add Artifact", "add-artifact")));
        
        Hyperlink hl = new Hyperlink("Add Workspace", "add-workspace");
        topMenuLinks.add(asHorizontal(addWkspcImg, new Label(" "), hl));
        
        addOtherLinks(topMenuLinks);
        
        if (showBrowse) {
            hl = new Hyperlink("Browse Workspaces", "browse");
            topMenuLinks.add(hl);
        }

        if (showSearch) {
            hl = new Hyperlink("Search Workspaces", "search");
            topMenuLinks.add(hl);
        }
        
        addMenuItem(topMenuLinks);
    }

    protected void addOtherLinks(Toolbox topMenuLinks) {
        
    }

}
