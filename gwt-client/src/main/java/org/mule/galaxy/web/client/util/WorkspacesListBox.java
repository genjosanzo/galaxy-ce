package org.mule.galaxy.web.client.util;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.rpc.WWorkspace;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

public class WorkspacesListBox extends Composite {

    private ListBox workspacesLB;

    public WorkspacesListBox(Collection workspaces, 
                             String childrenToHideId, 
                             String selectedWorkspaceId,
                             boolean allowNoWorkspace) {
        super();
        
        workspacesLB = new ListBox();
        
        if (allowNoWorkspace) {
            workspacesLB.addItem("[No parent]");
        }
        
        addWorkspaces(workspaces, workspacesLB, selectedWorkspaceId, childrenToHideId);
        
        if ("".equals(selectedWorkspaceId) || selectedWorkspaceId == null) {
            workspacesLB.setSelectedIndex(0);
        }
        
        initWidget(workspacesLB);
    }

    private void addWorkspaces(final Collection workspaces, 
                               ListBox workspacesLB, 
                               String selectedWorkspaceId,
                               String childrenToHideId) {
        for (Iterator itr = workspaces.iterator(); itr.hasNext();) {
            WWorkspace w = (WWorkspace) itr.next();
            
            if (childrenToHideId == null || !childrenToHideId.equals(w.getId())) {
                workspacesLB.addItem(w.getPath(), w.getId());
                
                if (w.getId().equals(selectedWorkspaceId)) {
                    workspacesLB.setSelectedIndex(workspacesLB.getItemCount() - 1);
                }
                
                Collection children = w.getWorkspaces();
                if (children != null && children.size() > 0) {
                    addWorkspaces(children, workspacesLB, selectedWorkspaceId, childrenToHideId);
                }
            }
        }
    }

    public String getSelectedValue() {
        return workspacesLB.getValue(workspacesLB.getSelectedIndex());
    }

    public void setName(String string) {
        workspacesLB.setName(string);
    }
}
