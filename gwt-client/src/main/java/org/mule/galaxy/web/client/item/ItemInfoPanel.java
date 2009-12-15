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

package org.mule.galaxy.web.client.item;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.AbstractShowable;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.property.EntryMetadataPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextArea;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.WComment;

public class ItemInfoPanel extends AbstractShowable {

    private HorizontalPanel topPanel;
    private Galaxy galaxy;
    private VerticalPanel rightGroup;
    private VerticalPanel panel;
    private FlowPanel commentsPanel;
    private ItemInfo info;
    private final RepositoryMenuPanel menuPanel;
    
    public ItemInfoPanel(final Galaxy galaxy,
                         RepositoryMenuPanel menuPanel,
                         ItemInfo item, 
                         final ItemPanel artifactPanel, 
                         final List<String> callbackParams) {
        this.galaxy = galaxy;
        this.menuPanel = menuPanel;
        this.info = item;
        
        panel = new VerticalPanel();
        
        topPanel = new HorizontalPanel();
        topPanel.setStyleName("artifactTopPanel");
        
        panel.add(createTitle("Details"));
        panel.add(topPanel);

        FlexTable table = createColumnTable();
        
        final NameEditPanel nep = new NameEditPanel(galaxy, 
                                                    menuPanel,
                                                    item.getId(),
                                                    item.getName(),
                                                    item.getParentPath(), 
                                                    callbackParams);
        
        table.setWidget(0, 0, new Label("Name:"));
        table.setWidget(0, 1, nep);
        
        table.setText(1, 0, "Type:");
        table.setText(1, 1, item.getType());
        
        styleHeaderColumn(table);
        topPanel.add(table);

        rightGroup = new VerticalPanel();
        rightGroup.setStyleName("artifactInfoRightGroup");
        rightGroup.setSpacing(6);
        
        topPanel.add(rightGroup);
        
        if (item.isLocal()) {
            panel.add(newSpacer());
            
            panel.add(new EntryMetadataPanel(galaxy, menuPanel, "Metadata", item, false));
            panel.add(newSpacer());
            
            initComments();
            panel.add(newSpacer());

        }
        initWidget(panel);
    }

    private void initComments() {
        SimplePanel commentsBase = new SimplePanel();
        commentsBase.setStyleName("comments-base");
        
        commentsPanel = new FlowPanel();
        commentsPanel.setStyleName("comments");
        commentsBase.add(commentsPanel);
        
        Hyperlink addComment = new Hyperlink("Add", History.getToken());
        addComment.addClickListener(new AddCommentClickListener(commentsPanel, null));
        
        InlineFlowPanel commentTitlePanel = createTitleWithLink("Comments", addComment);
        Image img = new Image("images/feed-icon-14x14.png");
        img.setStyleName("feed-icon");
        img.setTitle("Comments Atom Feed");
        img.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent clickEvent) {
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

        Image img = new Image("images/comment_blue.gif");
        
        InlineFlowPanel title = new InlineFlowPanel();
        title.setStyleName("commentTitle");
        Label userLabel = new Label(" " + c.getUser());
        Label dateLabel = new Label(" at " + c.getDate());
        userLabel.setStyleName("user");
        
        Hyperlink replyLink = new Hyperlink("Reply", History.getToken());
        replyLink.addClickListener(new AddCommentClickListener(commentPanel, c.getId()));
        title.add(img);
        title.add(replyLink);
        title.add(userLabel);
        title.add(dateLabel);

        commentPanel.add(title);
        
        Label commentBody = new Label(c.getText(), true);
        commentBody.setStyleName("commentText");
        
        commentPanel.add(commentBody);

        for (WComment child : c.getComments()) {
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
        cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                commentPanel.remove(addCommentPanel);
                replyClickListener.setShowingComment(false);
            }
        });


        final Button addButton = new Button("Save");
        addButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {

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
        
        galaxy.getRegistryService().addComment(info.getId(), parentId, text.getTextArea().getText(), new AbstractCallback(menuPanel) {

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
