package org.mule.galaxy.web.client.workspace;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.registry.RegistryMenuPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.WorkspacesListBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WWorkspace;

public class WorkspaceForm extends AbstractErrorShowingComposite {

    private TextBox workspaceTextBox;
    private Galaxy galaxy;
    private boolean edit;
    private ListBox lifecyclesLB;
    private FlowPanel panel;
    private Collection workspaces;
    private String parentWorkspaceId;
    private WWorkspace workspace;
    private String workspaceId;

    /**
     * Set up the form for adding a workspace.
     * @param galaxy
     */
    public WorkspaceForm(final Galaxy galaxy) {
        this.galaxy = galaxy;
        this.edit = false;
        
        panel = new FlowPanel();
        RegistryMenuPanel menuPanel = new RegistryMenuPanel();
        menuPanel.setMain(panel);
        
        initWidget(menuPanel);
    }
    
    /**
     * Set up the form for editing a workspace.
     * @param parentId 
     */
    public WorkspaceForm(Galaxy galaxy, 
                         Collection workspaces, 
                         WWorkspace workspace, 
                         String parentWorkspaceId) {
        this.galaxy = galaxy;
        this.edit = true;
        this.workspaces = workspaces;
        this.workspace = workspace;
        this.workspaceId = workspace.getId();
        this.parentWorkspaceId = parentWorkspaceId;
        
        panel = new FlowPanel();
        
        initWidget(panel);
    }

    public void onShow(List params) {
        panel.clear();
        panel.add(new Label("Loading..."));
        
        if (params.size() > 0 && !edit) {
            parentWorkspaceId = (String) params.get(0);
        }
        
        if (!edit || workspaces == null) {
            galaxy.getRegistryService().getWorkspaces(new AbstractCallback(this) {
                public void onSuccess(Object workspaces) {
                    loadWorkspaces((Collection) workspaces);
                }
            });
        } else {
            loadWorkspaces(workspaces);
        }
    }
    
    public void loadWorkspaces(Collection workspaces) {
        panel.clear();
        this.workspaces = workspaces;
        
        if (!edit) {
            panel.add(createTitle("Add Workspace"));
        }
        
        final FlexTable table = createColumnTable();
        
        final WorkspacesListBox workspacesLB = 
            new WorkspacesListBox(workspaces, workspaceId, parentWorkspaceId, true);
        
        table.setText(0, 0, "Parent Workspace:");
        table.setWidget(0, 1, workspacesLB);
        
        table.setText(1, 0, "Workspace Name:");
        
        workspaceTextBox = new TextBox();
        table.setWidget(1, 1, workspaceTextBox);
        
        if (edit) {
            workspaceTextBox.setText(workspace.getName());
        }

        table.setText(2, 0, "Default Lifecycle:");
        galaxy.getRegistryService().getLifecycles(new AbstractCallback(this) {
            public void onSuccess(Object o) {
                loadLifecycles(table, (Collection) o);
            }
        });
        panel.add(table);
        
        InlineFlowPanel buttonPanel = new InlineFlowPanel();
        Button saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                save(workspacesLB.getSelectedValue(), 
                     workspaceTextBox.getText());
            }
        });
        buttonPanel.add(saveButton);
        
        if (edit) {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(new ClickListener() {

                public void onClick(Widget arg0) {
                    showDeleteDialog(workspaceId);
                }
                
            });
            buttonPanel.add(deleteButton);
        }
        table.setWidget(3, 1, buttonPanel);

        if (edit) {
            setTitle("Edit Workspace " + workspace.getName());
        } else {
            setTitle("Add Workspace");
        }
        
        styleHeaderColumn(table);
    }

    protected void loadLifecycles(FlexTable table, Collection lifecycles) {
        lifecyclesLB = new ListBox();
        lifecyclesLB.addItem("[Inherit From Parent]", "inherit");
        
        for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
            WLifecycle l = (WLifecycle)itr.next();
            
            lifecyclesLB.addItem(l.getName(), l.getId());
            
            if (workspace != null && l.getId().equals(workspace.getDefaultLifecycleId())) {
                lifecyclesLB.setSelectedIndex(lifecyclesLB.getItemCount()-1);
            }
        }
        
        table.setWidget(2, 1, lifecyclesLB);
    }

    protected void showDeleteDialog(String workspaceId) {
        final DeleteDialog popup = new DeleteDialog(this, workspaceId);
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 3;
                int top = (Window.getClientHeight() - offsetHeight) / 3;
                popup.setPopupPosition(left, top);
            }
        });
    }

    protected void save(String parentWorkspaceId, final String text) {
        AbstractCallback callback = new AbstractCallback(this) {

            public void onSuccess(Object arg0) {
                History.newItem("browse");
            }
            
        };
        
        String lifecycleId = null;
        int idx = lifecyclesLB.getSelectedIndex();
        if (idx > 0) {
            lifecycleId = lifecyclesLB.getValue(idx);
        }
        
        if (edit) {
            galaxy.getRegistryService().updateWorkspace(workspace.getId(), 
                                                        parentWorkspaceId,
                                                        text, 
                                                        lifecycleId,
                                                        callback);
        } else {
            galaxy.getRegistryService().addWorkspace(parentWorkspaceId, text, lifecycleId, callback);
        }
    }
    
    protected void delete(String workspaceId2) {
        galaxy.getRegistryService().deleteWorkspace(workspaceId2, new AbstractCallback(this) {

            public void onSuccess(Object arg0) {
                galaxy.setMessageAndGoto("browse", "Workspace was deleted.");
            }
            
        });
    }

    private static class DeleteDialog extends DialogBox {

        public DeleteDialog(final WorkspaceForm panel, final String workspaceId) {
          // Set the dialog box's caption.
          setText("Are you sure you want to delete this workspace and all it's artifacts?");

          InlineFlowPanel buttonPanel = new InlineFlowPanel();

          Button no = new Button("No");
          no.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                DeleteDialog.this.hide();
            }
          });
          
          Button yes = new Button("Yes");
          yes.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                DeleteDialog.this.hide();
                panel.delete(workspaceId);
            }
          });
          buttonPanel.add(no);
          buttonPanel.add(yes);
          
          setWidget(buttonPanel);
        }
      }
}