package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WPermission;
import org.mule.galaxy.web.rpc.WPermissionGrant;

public class GroupPanel extends AbstractFlowComposite {

    private final AdministrationPanel adminPanel;
    private FlexTable table;
    private Collection permissions;
    private Button applyButton;
    private List rows;
    private Map groups2Permissions;
    private Button resetButton;

    public GroupPanel(AdministrationPanel adminPanel) {
        super();
        this.adminPanel = adminPanel;
    }

    public void onShow() {
        // table.setStyleName("permission-grant-table");
        panel.clear();
        panel.add(new Label("Loading..."));
        
        adminPanel.getUserService().getPermissions(new AbstractCallback(adminPanel) {
            public void onSuccess(Object permissions) {
                receivePermissions((Collection) permissions);
            }
        });
    }

    protected void receivePermissions(Collection permissions) {
        this.permissions = permissions;
        
        adminPanel.getUserService().getGroupPermissions(new AbstractCallback(adminPanel) {
            public void onSuccess(Object groups) {
                receiveGroups((Map) groups);
            }
        });
    }

    protected void receiveGroups(Map groups2Permissions) {
        this.groups2Permissions = groups2Permissions;
        panel.clear();
        
        table = createTitledRowTable(panel, "Manage Group Permissions");
        int col = 1;
        for (Iterator itr = permissions.iterator(); itr.hasNext();) {
            WPermission p = (WPermission)itr.next();
            table.setText(0, col, p.getDescription());
            
            col++;
        }   
        
        rows = new ArrayList();
        for (Iterator itr = groups2Permissions.keySet().iterator(); itr.hasNext();) {
            rows.add(((WGroup)itr.next()).getName());
        }
        Collections.sort(rows);
        
        for (Iterator itr = groups2Permissions.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry)itr.next();
            
            final WGroup group = (WGroup) e.getKey();

            final Hyperlink hl = new Hyperlink(group.getName(), "edit-group-" + group.getId());
            hl.addClickListener(new ClickListener() {
                public void onClick(Widget arg0) {
                    GroupForm form = new GroupForm(adminPanel, group, false);
                    adminPanel.createPageInfo(hl.getTargetHistoryToken(), form); 
                }
            });
            
            int row = rows.indexOf(group.getName()) + 1;
            table.setWidget(row, 0, hl);
            
            Collection grants = (Collection) e.getValue();
            for (Iterator gItr = grants.iterator(); gItr.hasNext();) {
                WPermissionGrant pg = (WPermissionGrant)gItr.next();

                table.setWidget(row, getPermissionColumn(pg.getPermission()), createGrantListBox(pg));
            }
            row++;
        }
        
        table.getFlexCellFormatter().setColSpan(rows.size() + 1, 0, col);
        
        applyButton = new Button("Apply");
        applyButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                beginApply();
            }
        });
        
        resetButton = new Button("Reset");
        resetButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                reset();
            }
        });
        
        table.setWidget(rows.size() + 1, 0, asHorizontal(applyButton, resetButton));
        
    }

    /**
     * Go back to the saved state on the server.
     */
    protected void reset() {
        onShow();
    }

    /**
     * Update the permission map and then save it.
     */
    protected void beginApply() {
        setEnabled(false);
        for (Iterator itr = groups2Permissions.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry)itr.next();
            
            WGroup g = (WGroup) e.getKey();
            Collection permGrants = (Collection) e.getValue();
            
            int row = rows.indexOf(g.getName());
            
            for (Iterator pgItr = permGrants.iterator(); pgItr.hasNext();) {
                WPermissionGrant pg = (WPermissionGrant)pgItr.next();
                
                int col = getPermissionColumn(pg.getPermission());
                
                CheckBox cb = (CheckBox) table.getWidget(row + 1, col);
                
                if (cb.isChecked()) {
                    pg.setGrant(WPermissionGrant.GRANTED);
                } else {
                    pg.setGrant(WPermissionGrant.REVOKED);
                }
            }
        }
        
        adminPanel.getUserService().applyPermissions(groups2Permissions, new AbstractCallback(adminPanel) {

            public void onSuccess(Object arg0) {
                setEnabled(true);
            }
            
        });
    }

    protected void setEnabled(boolean e) {
        applyButton.setEnabled(e);
        resetButton.setEnabled(e);
    }

    private int getPermissionColumn(String permission) {
        int i = 1;
        for (Iterator itr = permissions.iterator(); itr.hasNext();) {
            WPermission p = (WPermission)itr.next();
            
            if (p.getName().equals(permission)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private Widget createGrantListBox(WPermissionGrant pg) {
        CheckBox cb = new CheckBox();
        if (pg.getGrant() == WPermissionGrant.GRANTED) {
            cb.setChecked(true);
        }
        return cb;
    }

}
