package org.mule.galaxy.web.client.artifact;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ArtifactForm;
import org.mule.galaxy.web.client.MenuPanelPageInfo;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.WorkspacePanel;
import org.mule.galaxy.web.client.util.ExternalHyperlink;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.DependencyInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.WComment;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

public class ArtifactInfoPanel extends AbstractComposite {


    private HorizontalPanel topPanel;
    private RegistryPanel registryPanel;
    private VerticalPanel rightGroup;
    private VerticalPanel panel;
    private FlowPanel commentsPanel;
    private ExtendedArtifactInfo info;
    private ArtifactVersionInfo version;

    public ArtifactInfoPanel(final RegistryPanel registryPanel, 
                             ArtifactGroup group,
                             ExtendedArtifactInfo info, 
                             ArtifactVersionInfo version) {
        this.registryPanel = registryPanel;
        this.info = info;
        this.version = version;
        
        panel = new VerticalPanel();
        
        topPanel = new HorizontalPanel();
        topPanel.setStyleName("artifactTopPanel");
        
        panel.add(createTitle("Details"));
        panel.add(topPanel);

        FlexTable table = createColumnTable();
        
        final NameEditPanel nep = new NameEditPanel(registryPanel, info.getId(), 
                                                    (String) info.getValue(0),
                                                    info.getWorkspaceId());
        
        table.setWidget(0, 0, new Label("Name:"));
        table.setWidget(0, 1, nep);
        
        for (int i = 1; i < group.getColumns().size(); i++) {
            table.setText(i, 0, (String) group.getColumns().get(i) + ":");
        }
        
        int c = 1;
        for (; c < group.getColumns().size(); c++) {
            table.setText(c, 1, info.getValue(c));
        }
        
        table.setWidget(c, 0, new Label("Description:"));
        FlowPanel descPanel = new FlowPanel();
        table.setWidget(c, 1, descPanel);
        initDescription(descPanel);
        
        styleHeaderColumn(table);
        topPanel.add(table);
        
        rightGroup = new VerticalPanel();
        rightGroup.setStyleName("artifactInfoRightGroup");
        rightGroup.setSpacing(6);
        
        addArtifactLinks(registryPanel);
        
        topPanel.add(rightGroup);
        
        registryPanel.getRegistryService().getDependencyInfo(info.getId(), new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initDependencies((Collection) o);
            }
            
        });
        
        panel.add(new ArtifactMetadataPanel(registryPanel, info, version));
        
        initComments();
        
        initWidget(panel);
    }

    private void initDescription(final FlowPanel descPanel) {
        descPanel.clear();
        descPanel.add(new Label(info.getDescription()));
        
        Hyperlink hl = new Hyperlink("Edit", "edit-description-" + info.getId());
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                initDescriptionForm(descPanel);
            }
            
        });
        descPanel.add(hl);
    }

    private void initComments() {
        SimplePanel commentsBase = new SimplePanel();
        commentsBase.setStyleName("comments-base");
        
        commentsPanel = new FlowPanel();
        commentsPanel.setStyleName("comments");
        commentsBase.add(commentsPanel);
        
        Hyperlink addComment = new Hyperlink("Add", "add-comment");
        addComment.addClickListener(new AddCommentClickListener(commentsPanel, null));
        
        InlineFlowPanel commentTitlePanel = createTitleWithLink("Comments", addComment);
        Image img = new Image("images/feed-icon-14x14.png");
        img.setStyleName("feed-icon");
        img.setTitle("Comments Atom Feed");
        img.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                Window.open(info.getCommentsFeedLink(), null, "scrollbars=yes");
            }
            
        });
        commentTitlePanel.add(img);
        
        panel.add(commentTitlePanel);
        panel.add(commentsBase);
        
        for (Iterator itr = info.getComments().iterator(); itr.hasNext();) {
            commentsPanel.add(createCommentPanel((WComment) itr.next()));
        }
    }

    private Widget createCommentPanel(WComment c) {
        final FlowPanel commentPanel = new FlowPanel();
        commentPanel.setStyleName("comment");
        
        InlineFlowPanel title = new InlineFlowPanel();
        title.setStyleName("commentTitle");
        Label userLabel = new Label(c.getUser());
        Label dateLabel = new Label(" at " + c.getDate());
        userLabel.setStyleName("user");
        
        Hyperlink replyLink = new Hyperlink("Reply", "reply-" + c.getId());
        replyLink.addClickListener(new AddCommentClickListener(commentPanel, c.getId()));
        title.add(replyLink);
        title.add(userLabel);
        title.add(dateLabel);
        
        commentPanel.add(title);
        
        Label commentBody = new Label(c.getText(), true);
        commentBody.setStyleName("commentText");
        
        commentPanel.add(commentBody);
        
        for (Iterator comments = c.getComments().iterator(); comments.hasNext();) {
            WComment child = (WComment) comments.next();
            
            SimplePanel nestedComment = new SimplePanel();
            nestedComment.setStyleName("nestedComment");
            
            Widget childPanel = createCommentPanel(child);
            nestedComment.add(childPanel);
            
            commentPanel.add(nestedComment);
        }
        return commentPanel;
    }

    protected void showAddComment(final Panel commentPanel, 
                                  final String parentId,
                                  final AddCommentClickListener replyClickListener) {
        if (replyClickListener.isShowingComment()) {
            return;
        }
        
        replyClickListener.setShowingComment(true);
        final VerticalPanel addCommentPanel = new VerticalPanel();
        addCommentPanel.setStyleName("addComment");
        
        final TextArea text = new TextArea();
        text.setCharacterWidth(60);
        text.setVisibleLines(5);
        addCommentPanel.add(text);
        
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.setSpacing(10);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        
        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                commentPanel.remove(addCommentPanel);
                replyClickListener.setShowingComment(false);
            }
        });
        buttons.add(cancelButton);
        
        final Button addButton = new Button("Add");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                addComment(commentPanel, 
                           addCommentPanel, 
                           text,
                           cancelButton,
                           addButton,
                           parentId,
                           replyClickListener);
            }
        });
        buttons.add(addButton);
        addCommentPanel.add(buttons);
        addCommentPanel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);

        addCommentPanel.setVisible(true);
        
        if (!commentPanel.equals(commentsPanel)) {
            SimplePanel nested = new SimplePanel();
            nested.setStyleName("nestedComment");
            nested.add(addCommentPanel);
            commentPanel.add(addCommentPanel);
        } else {
            commentPanel.add(addCommentPanel);
        }
    }

    protected void addComment(final Panel parent,
                              final Panel addCommentPanel, 
                              final TextArea text, 
                              final Button cancelButton, 
                              final Button addButton, 
                              final String parentId,
                              final AddCommentClickListener replyClickListener) {

        cancelButton.setEnabled(false);
        addButton.setEnabled(false);
        text.setEnabled(false);
        
        registryPanel.getRegistryService().addComment(info.getId(), parentId, text.getText(), new AbstractCallback(registryPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                
                cancelButton.setEnabled(true);
                addButton.setEnabled(true);
                text.setEnabled(true);
            }

            public void onSuccess(Object o) {
                parent.remove(addCommentPanel);
                
                Widget commentPanel = createCommentPanel((WComment) o);
                if (replyClickListener.commentPanel != commentsPanel) {
                    SimplePanel nestedComment = new SimplePanel();
                    nestedComment.setStyleName("nestedComment");
                    nestedComment.add(commentPanel);
                    commentPanel = nestedComment;
                }
                
                parent.add(commentPanel);
                replyClickListener.setShowingComment(false);
            }
            
        });
    }

    private void addArtifactLinks(final RegistryPanel registryPanel) {
        Hyperlink hl = new Hyperlink("View Artifact", "view-artifact");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                Window.open(info.getArtifactLink(), null, "scrollbars=yes");
            }
            
        });
        
        rightGroup.add(hl);

        ExternalHyperlink permalink = new ExternalHyperlink("Permalink", info.getArtifactLink());
        permalink.setTitle("Direct artifact link for inclusion in email, etc.");
        rightGroup.add(permalink);
        
        hl = new Hyperlink("New Version", "new-artifact-version-"+info.getId());
        MenuPanelPageInfo newVersionPage = new MenuPanelPageInfo(hl, registryPanel) {
            public AbstractComposite createInstance() {
                return new ArtifactForm(registryPanel, info.getId());
            }
        };
        registryPanel.addPage(newVersionPage);
        rightGroup.add(hl);
        
        hl = new Hyperlink("Delete", "delete-artifact");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                warnDelete();
            }
            
        });
        rightGroup.add(hl);
    }
    
    protected void warnDelete() {
        if (Window.confirm("Are you sure you want to delete this artifact")) {
            registryPanel.getRegistryService().delete(info.getId(), new AbstractCallback(registryPanel) {

                public void onSuccess(Object arg0) {
                    registryPanel.setMain(new WorkspacePanel(registryPanel));
                    registryPanel.setMessage("Artifact was deleted.");
                }
                
            });
        }
    }

    protected void initDependencies(Collection o) {
        Toolbox depPanel = new Toolbox(true);
        depPanel.setTitle("Dependencies");
        
        Toolbox depOnPanel = new Toolbox(true);
        depOnPanel.setTitle("Depended On By");
        
        boolean addedDeps = false;
        boolean addedDependedOn = false;
        
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            final DependencyInfo info = (DependencyInfo) itr.next();
            
            Hyperlink hl = new Hyperlink(info.getArtifactName(), 
                                         "artifact-" + info.getArtifactId());
            hl.addClickListener(new ClickListener() {

                public void onClick(Widget arg0) {
                    registryPanel.setMain(new ArtifactPanel(registryPanel, 
                                                            info.getArtifactId()));
                }
            });
            
            if (info.isDependsOn()) {
                depPanel.add(hl);
                
                if (!addedDeps) {
                    rightGroup.add(depPanel);
                    addedDeps = true;
                }
            } else {
                depOnPanel.add(hl);
                
                if (!addedDependedOn) {
                    rightGroup.add(depOnPanel);
                    addedDependedOn = true;
                }
            }
        }
        topPanel.add(rightGroup);
    }

    private void initDescriptionForm(final FlowPanel descPanel) {
        descPanel.clear();
        
        final TextArea text = new TextArea();
        text.setCharacterWidth(60);
        text.setVisibleLines(8);
        text.setText(info.getDescription());
        descPanel.add(text);

        HorizontalPanel buttons = new HorizontalPanel();
        descPanel.add(buttons);
        buttons.setWidth("100%");
        buttons.setSpacing(10);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        
        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                initDescription(descPanel);
            }
        });
        buttons.add(cancelButton);
        
        final Button addButton = new Button("Save");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                saveDescription(descPanel, text, cancelButton, addButton);
            }

           
        });
        buttons.add(addButton);
    }

    protected void saveDescription(final FlowPanel descPanel, final TextArea text,
                                   final Button cancelButton, final Button addButton) {
        cancelButton.setEnabled(false);
        addButton.setEnabled(false);
       
        AbstractCallback callback = new AbstractCallback(registryPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                cancelButton.setEnabled(true);
                addButton.setEnabled(true);
            }

            public void onSuccess(Object arg0) {
                info.setDescription(text.getText());
                initDescription(descPanel);
            }

        };
        registryPanel.getRegistryService().setDescription(info.getId(), text.getText(), callback);
          
    }
    private final class AddCommentClickListener implements ClickListener {
        private final Panel commentPanel;
        private boolean showingComment;
        private String parentId;
        
        private AddCommentClickListener(Panel commentPanel, String parentId) {
            this.commentPanel = commentPanel;
            this.parentId = parentId;
        }

        public void onClick(Widget w) {
            showAddComment(commentPanel, parentId, this);
        }

        public boolean isShowingComment() {
            return showingComment;
        }

        public void setShowingComment(boolean showingComment) {
            this.showingComment = showingComment;
        }
        
    }
}
