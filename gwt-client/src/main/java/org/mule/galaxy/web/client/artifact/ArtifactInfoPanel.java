package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractCallback;
import org.mule.galaxy.web.client.AddArtifactPanel;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.DependencyInfo;
import org.mule.galaxy.web.client.ExtendedArtifactInfo;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.WComment;
import org.mule.galaxy.web.client.WProperty;

public class ArtifactInfoPanel extends Composite {


    private HorizontalPanel topPanel;
    private RegistryPanel registryPanel;
    private VerticalPanel rightGroup;
    private VerticalPanel panel;
    private VerticalPanel commentsPanel;
    private ExtendedArtifactInfo info;

    public ArtifactInfoPanel(final RegistryPanel registryPanel, 
                             ArtifactGroup group,
                             ExtendedArtifactInfo info) {
        this.registryPanel = registryPanel;
        this.info = info;
        
        panel = new VerticalPanel();
        
        topPanel = new HorizontalPanel();
        topPanel.setStyleName("artifactTopPanel");
        
        panel.add(topPanel);

        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(1);
        table.setCellPadding(0);
        table.setWidth("100%");
        table.getColumnFormatter().setStyleName(0, "artifactTableHeader");
        table.getColumnFormatter().setStyleName(1, "artifactTableEntry");
        
        for (int i = 0; i < group.getColumns().size(); i++) {
            table.setText(i, 0, (String) group.getColumns().get(i));
            
        }
        
        int c = 0;
        for (; c < group.getColumns().size(); c++) {
            table.setText(c, 1, info.getValue(c));
        }
        
        initDescription(table, c);
        
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
        
        
        
        initMetadata();
        initComments();
        
        initWidget(panel);
    }

    private void initDescription(FlexTable table, int c) {
        final SimplePanel descPanel = new SimplePanel();
        
        HorizontalPanel descLabelPanel = new HorizontalPanel();
        descLabelPanel.setStyleName("artifactDescriptionPanel");
        descLabelPanel.add(new Label("Description ["));
        Hyperlink hl = new Hyperlink("Edit", "");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                initDescriptionForm(descPanel);
            }
            
        });
        descLabelPanel.add(hl);
        descLabelPanel.add(new Label("]"));
        
        table.setWidget(c, 0, descLabelPanel);
        descPanel.add(new Label(info.getDescription()));
        table.setWidget(c, 1, descPanel);
    }

    private void initMetadata() {
        Label label = new Label("Metadata");
        label.setStyleName("right-title");

        panel.add(label);
        
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(1);
        table.setCellPadding(0);
        table.setWidth("100%");
        table.getColumnFormatter().setStyleName(0, "artifactTableHeader");
        table.getColumnFormatter().setStyleName(1, "artifactTableEntry");
        
        int i = 0;
        for (Iterator itr = info.getProperties().iterator(); itr.hasNext();) {
            WProperty p = (WProperty) itr.next();
            
            table.setText(i, 0, p.getDescription());
            String txt = p.getValue();
            Widget w = null;
            if (p.isLocked()) {
                if ("".equals(txt) || txt == null) {
                    txt = "[no value]";
                }
                txt += " [Locked]";
                w = new Label(txt);
            } else {
                txt += " ";
                Hyperlink hl = new Hyperlink("Edit", "edit-property");
                hl.setStyleName("editPropertyLink");
                hl.addClickListener(new ClickListener() {

                    public void onClick(Widget w) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                });
                HorizontalPanel value = new HorizontalPanel();
                value.add(new Label(txt));
                value.add(hl);
                w = value;
            }
            
            table.setWidget(i, 1, w);
            
            i++;
        }
        panel.add(table);
    }

    private void initComments() {
        commentsPanel = new VerticalPanel();
        commentsPanel.setStyleName("comments");

        Label label = new Label("Comments");
        label.setStyleName("right-title");
        panel.add(label);
        
        Hyperlink addComment = new Hyperlink("Add", "add-comment");
        addComment.addClickListener(new AddCommentClickListener(commentsPanel, null));
        addComment.setStyleName("addCommentLink");
        panel.add(addComment);
        
        panel.add(commentsPanel);
        
        for (Iterator itr = info.getComments().iterator(); itr.hasNext();) {
            commentsPanel.add(createCommentPanel((WComment) itr.next()));
        }
    }

    private Widget createCommentPanel(WComment c) {
        final VerticalPanel commentPanel = new VerticalPanel();
        commentPanel.setStyleName("comment");
        
        HorizontalPanel title = new HorizontalPanel();
        
        Label userDateLabel = new Label(c.getUser() + " on " + c.getDate());
        
        Hyperlink replyLink = new Hyperlink("Reply", "reply-" + c.getId());
        replyLink.addClickListener(new AddCommentClickListener(commentPanel, c.getId()));
        title.add(userDateLabel);
        title.add(replyLink);
//        title.setCellHorizontalAlignment(replyLink, HasHorizontalAlignment.ALIGN_RIGHT);
//        title.setWidth("100%");
        
        SimplePanel titlePanel = new SimplePanel();
        titlePanel.setStyleName("commentTitle");
        titlePanel.add(title);
        
        commentPanel.add(titlePanel);
        
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
        if (replyClickListener.isShowingComment())
            return;
        
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
        
        commentPanel.add(addCommentPanel);
    }

    protected void addComment(final Panel parent,
                              final VerticalPanel addCommentPanel, 
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
        Hyperlink hl = new Hyperlink("View", "view-artifact");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        rightGroup.add(hl);
        hl = new Hyperlink("New Version", "view-artifact");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                registryPanel.setMain(new AddArtifactPanel());
            }
            
        });
        rightGroup.add(hl);
    }
    
    protected void initDependencies(Collection o) {
        VerticalPanel depPanel = new VerticalPanel();
        
        Label label = new Label("Dependencies");
        label.setStyleName("dependencyPanelHeader");
        depPanel.add(label);
        depPanel.setStyleName("dependencyPanel");
        
        VerticalPanel depOnPanel = new VerticalPanel();
        label = new Label("Depended On By");
        label.setStyleName("dependencyPanelHeader");
        depOnPanel.add(label);
        depOnPanel.setStyleName("dependencyPanel");
        
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

    private void initDescriptionForm(final SimplePanel descPanel) {
        descPanel.clear();
        
        VerticalPanel form = new VerticalPanel();
        final TextArea text = new TextArea();
        text.setCharacterWidth(40);
        text.setVisibleLines(8);
        text.setText(info.getDescription());
        form.add(text);

        HorizontalPanel buttons = new HorizontalPanel();
        form.add(buttons);
        buttons.setWidth("100%");
        buttons.setSpacing(10);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        
        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                descPanel.clear();
                descPanel.add(new Label(info.getDescription()));
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
        
        descPanel.add(form);
    }

    protected void saveDescription(final SimplePanel descPanel, final TextArea text,
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
                descPanel.clear();
                descPanel.add(new Label(text.getText()));
            }

        };
        registryPanel.getRegistryService().setDescription(info.getId(), text.getText(), callback);
          
    }
    private final class AddCommentClickListener implements ClickListener {
        private final VerticalPanel commentPanel;
        private boolean showingComment;
        private String parentId;
        
        private AddCommentClickListener(VerticalPanel commentPanel, String parentId) {
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
