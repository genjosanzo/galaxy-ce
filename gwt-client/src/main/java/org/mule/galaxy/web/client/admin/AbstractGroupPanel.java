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
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WPermission;
import org.mule.galaxy.web.rpc.WPermissionGrant;

public abstract class AbstractGroupPanel extends AbstractFlowComposite {

    protected final AbstractMenuPanel menuPanel;
    protected FlexTable table;
    private Collection permissions;
    private Button applyButton;
    private List rows;
    private Map groups2Permissions;
    private Button resetButton;

    public AbstractGroupPanel(AbstractMenuPanel adminPanel) {
        super();
        this.menuPanel = adminPanel;
    }

    public void onShow() {
        // table.setStyleName("permission-grant-table");
        panel.clear();
        panel.add(new Label("Loading..."));
        
        AbstractCallback callback = new AbstractCallback(menuPanel) {
            public void onSuccess(Object permissions) {
                receivePermissions((Collection) permissions);
            }
        };
        getPermissions(callback);
    }

    protected void receivePermissions(Collection permissions) {
        this.permissions = permissions;
        AbstractCallback callback = new AbstractCallback(menuPanel) {
            public void onSuccess(Object groups) {
                receiveGroups((Map) groups);
            }
        };
        
        getGroupPermissionGrants(callback);
    }

    protected abstract void getPermissions(AbstractCallback callback);

    protected abstract void getGroupPermissionGrants(AbstractCallback callback);
    
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
                    GroupForm form = new GroupForm(menuPanel, group, false);
                    menuPanel.createPageInfo(hl.getTargetHistoryToken(), form); 
                }
            });
            
            int row = rows.indexOf(group.getName()) + 1;
            table.setWidget(row, 0, hl);
            
            Collection grants = (Collection) e.getValue();
            for (Iterator gItr = grants.iterator(); gItr.hasNext();) {
                WPermissionGrant pg = (WPermissionGrant)gItr.next();

                table.setWidget(row, getPermissionColumn(pg.getPermission()), createGrantWidget(pg));
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
                
                setGrant(row, col, pg);
            }
        }
        
        AbstractCallback callback = new AbstractCallback(menuPanel) {
            
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                setEnabled(true);
            }

            public void onSuccess(Object arg0) {
                setEnabled(true);
            }
        };
        applyPermissions(groups2Permissions, callback);
    }

    protected abstract void applyPermissions(Map groups2Permissions, AbstractCallback callback);

    protected abstract void setGrant(int row, int col, WPermissionGrant pg);

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

    protected abstract Widget createGrantWidget(WPermissionGrant pg);

}
