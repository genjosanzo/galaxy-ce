package org.mule.galaxy.web.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.util.InlineFlowPanel;

public class EditWorkspacePanel extends AbstractTitledComposite {

    private TextBox workspaceTextBox;
    private RegistryPanel registryPanel;
    private boolean edit;
    private String workspaceId;

    public EditWorkspacePanel(final RegistryPanel registryPanel,
                              final Collection workspaces,
                              final String parentWorkspaceId) {
        this(registryPanel, workspaces, parentWorkspaceId, null, null, false);
    }

    public EditWorkspacePanel(final RegistryPanel registryPanel,
                              final Collection workspaces,
                              final String parentWorkspaceId,
                              final String workspaceId,
                              final String workspaceName) {
        this(registryPanel, workspaces, parentWorkspaceId, 
             workspaceId, workspaceName, true);
    }
    
    protected EditWorkspacePanel(final RegistryPanel registryPanel,
                                 final Collection workspaces,
                                 final String parentWorkspaceId,
                                 final String workspaceId,
                                 final String workspaceName,
                                 boolean edit) {
        super();
        this.edit = edit;
        this.registryPanel = registryPanel;
        this.workspaceId = workspaceId;
        
        
        FlowPanel panel = new FlowPanel();
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(1);
        table.setCellPadding(0);
        table.getColumnFormatter().setStyleName(0, "artifactTableHeader");
        table.getColumnFormatter().setStyleName(1, "artifactTableEntry");
        
        
        final ListBox workspacesLB = new ListBox();
        workspacesLB.addItem("[No parent]");
        
        addWorkspaces(workspaces, workspacesLB, parentWorkspaceId, workspaceId);
        
        if ("".equals(parentWorkspaceId) || parentWorkspaceId == null) {
            workspacesLB.setSelectedIndex(0);
        }
        
        table.setText(0, 0, "Parent Workspace:");
        table.setWidget(0, 1, workspacesLB);
        
        table.setText(1, 0, "Workspace Name:");
        
        workspaceTextBox = new TextBox();
        table.setWidget(1, 1, workspaceTextBox);
        
        if (edit) {
            workspaceTextBox.setText(workspaceName);
        }
        
        panel.add(table);
        
        
        InlineFlowPanel buttonPanel = new InlineFlowPanel();
        Button saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                save(workspacesLB.getValue(workspacesLB.getSelectedIndex()), 
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
        table.setWidget(2, 1, buttonPanel);
        
        initWidget(panel);

        if (edit) {
            setTitle("Edit Workspace " + workspaceName);
        } else {
            setTitle("Add Workspace");
        }
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

    private void addWorkspaces(final Collection workspaces, 
                               ListBox workspacesLB, 
                               String workspaceId,
                               String childId) {
        for (Iterator itr = workspaces.iterator(); itr.hasNext();) {
            WWorkspace w = (WWorkspace) itr.next();
            
            if (childId == null || !childId.equals(w.getId())) {
                workspacesLB.addItem(w.getPath(), w.getId());
                
                if (w.getId().equals(workspaceId)) {
                    workspacesLB.setSelectedIndex(workspacesLB.getItemCount() - 1);
                }
                
                Collection children = w.getWorkspaces();
                if (children != null && children.size() > 0) {
                    addWorkspaces(children, workspacesLB, workspaceId, childId);
                }
            }
        }
    }

    protected void save(String parentWorkspaceId, final String text) {
        AbstractCallback callback = new AbstractCallback(registryPanel) {

            public void onSuccess(Object arg0) {
                registryPanel.refreshWorkspaces();
                registryPanel.setMain(new WorkspacePanel(registryPanel));
                registryPanel.setMessage("Saved workspace " + text);
            }
            
        };
        
        if (edit) {
            registryPanel.getRegistryService().updateWorkspace(workspaceId, 
                                                               parentWorkspaceId,
                                                               text, 
                                                               callback);
        } else {
            registryPanel.getRegistryService().addWorkspace(parentWorkspaceId, text, callback);
        }
    }
    protected void delete(String workspaceId2) {
        registryPanel.getRegistryService().deleteWorkspace(workspaceId2, new AbstractCallback(registryPanel) {

            public void onSuccess(Object arg0) {
                registryPanel.refreshWorkspaces();
                registryPanel.setMain(new WorkspacePanel(registryPanel));
                registryPanel.setMessage("Workspace was deleted.");
            }
            
        });
        
    }

    private static class DeleteDialog extends DialogBox {

        public DeleteDialog(final EditWorkspacePanel panel, final String workspaceId) {
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