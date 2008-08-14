/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.client.entry;

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

import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.property.EntryMetadataPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextArea;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.EntryGroup;
import org.mule.galaxy.web.rpc.EntryVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedEntryInfo;
import org.mule.galaxy.web.rpc.WComment;

public class EntryInfoPanel extends AbstractComposite {

    private HorizontalPanel topPanel;
    private Galaxy galaxy;
    private VerticalPanel rightGroup;
    private VerticalPanel panel;
    private FlowPanel commentsPanel;
    private ExtendedEntryInfo info;
    private final ErrorPanel errorPanel;
    
    public EntryInfoPanel(final Galaxy galaxy,
                          ErrorPanel errorPanel,
                          ExtendedEntryInfo info,
                          EntryVersionInfo version, 
                          final EntryPanel artifactPanel, 
                          final List<String> callbackParams) {
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.info = info;
        
        panel = new VerticalPanel();
        
        topPanel = new HorizontalPanel();
        topPanel.setStyleName("artifactTopPanel");
        
        panel.add(createTitle("Details"));
        panel.add(topPanel);

        FlexTable table = createColumnTable();
        
        final NameEditPanel nep = new NameEditPanel(galaxy, 
                                                    errorPanel,
                                                    version.getId(), 
                                                    (String) info.getValue(0),
                                                    version.getVersionLabel(),
                                                    info.getWorkspaceId(), artifactPanel, callbackParams);
        
        table.setWidget(0, 0, new Label("Name:"));
        table.setWidget(0, 1, nep);
        
        table.setText(1, 0, "Type:");
        if (info.isArtifact()) {
            table.setText(1, 1, info.getType() + " (" + info.getMediaType() + ")");
        } else {
            table.setText(1, 1, info.getType());
        }
        
        table.setWidget(2, 0, new Label("Description:"));
        FlowPanel descPanel = new FlowPanel();
        table.setWidget(2, 1, descPanel);
        initDescription(descPanel);
        
        styleHeaderColumn(table);
        topPanel.add(table);

        rightGroup = new VerticalPanel();
        rightGroup.setStyleName("artifactInfoRightGroup");
        rightGroup.setSpacing(6);
        
        topPanel.add(rightGroup);
        
        panel.add(newSpacer());
        
        panel.add(new EntryMetadataPanel(galaxy, errorPanel, "Metadata", info, false));
        panel.add(newSpacer());
        
        panel.add(new EntryMetadataPanel(galaxy, errorPanel, "Versioned Metadata", version, version.isIndexInformationStale()));
        panel.add(newSpacer());
        
        initComments();
        
        initWidget(panel);
    }

    private Widget newSpacer() {
        FlowPanel p = new FlowPanel();
//        p.setStyleName("spacer");
        p.add(new Label(" "));
        return p;
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
        
        for (Iterator<WComment> itr = info.getComments().iterator(); itr.hasNext();) {
            commentsPanel.add(createCommentPanel(itr.next()));
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
        
        for (Iterator<WComment> comments = c.getComments().iterator(); comments.hasNext();) {
            WComment child = comments.next();
            
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

        final ValidatableTextArea textArea = new ValidatableTextArea(new StringNotEmptyValidator());
        textArea.getTextArea().setCharacterWidth(60);
        textArea.getTextArea().setVisibleLines(5);

        addCommentPanel.add(textArea);
        
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

        final Button addButton = new Button("Save");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {

                if (!validateComment(textArea)) {
                    return;
                }

                addComment(commentPanel,
                           addCommentPanel,
                           textArea,
                           cancelButton,
                           addButton,
                           parentId,
                           replyClickListener);
            }
        });

        buttons.add(addButton);
        buttons.add(cancelButton);
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
                              final ValidatableTextArea text,
                              final Button cancelButton, 
                              final Button addButton, 
                              final String parentId,
                              final AddCommentClickListener replyClickListener) {

        cancelButton.setEnabled(false);
        addButton.setEnabled(false);
        text.getTextArea().setEnabled(false);
        
        galaxy.getRegistryService().addComment(info.getId(), parentId, text.getTextArea().getText(), new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                
                cancelButton.setEnabled(true);
                addButton.setEnabled(true);
                text.getTextArea().setEnabled(true);
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

    protected boolean validateComment(ValidatableTextArea textArea) {
        boolean isOk = true;
        isOk &= textArea.validate();
        return isOk;
    }

    private void initDescriptionForm(final FlowPanel descPanel) {
        descPanel.clear();
        
        final TextArea text = new TextArea();
        text.setCharacterWidth(60);
        text.setVisibleLines(8);
        text.setText(info.getDescription());
        descPanel.add(text);

        InlineFlowPanel buttons = new InlineFlowPanel();
        descPanel.add(buttons);
        buttons.addStyleName("buttonRow");

        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                initDescription(descPanel);
            }
        });

        final Button addButton = new Button("Save");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                saveDescription(descPanel, text, cancelButton, addButton);
            }


        });
        buttons.add(addButton);
        buttons.add(cancelButton);
    }

    protected void saveDescription(final FlowPanel descPanel, final TextArea text,
                                   final Button cancelButton, final Button addButton) {
        cancelButton.setEnabled(false);
        addButton.setEnabled(false);
       
        AbstractCallback callback = new AbstractCallback(errorPanel) {

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
        galaxy.getRegistryService().setDescription(info.getId(), text.getText(), callback);
          
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
