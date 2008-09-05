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

package org.mule.galaxy.web.client.registry;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.EntryGroup;
import org.mule.galaxy.web.rpc.EntryInfo;
import org.mule.galaxy.web.rpc.WSearchResults;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ArtifactListPanel extends AbstractComposite implements ClickListener {

    private FlowPanel panel;
    private FlowPanel artifactPanel;
    private int resultStart = 0;
    // TODO make it a configurable parameter, maybe per-user?
    private int maxResults = 15;
    private final AbstractBrowsePanel browsePanel;
    private FlowPanel activityNavPanel;

    private final Galaxy galaxy;
    private FlowPanel bulkEditPanel;
    private boolean editable;

    private WSearchResults searchResults;
    private Hyperlink bulkEditLink;
    private Hyperlink editSelected;
    private Hyperlink cancelLink;
    private Hyperlink editAll;
    private List<ArtifactGroupListPanel> groupPanels;

    public ArtifactListPanel(AbstractBrowsePanel browsePanel, Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
        this.browsePanel = browsePanel;

        panel = new FlowPanel();

        SimplePanel artifactPanelBase = new SimplePanel();
        artifactPanelBase.setStyleName("artifact-panel-base");
        panel.add(artifactPanelBase);

        artifactPanel = new FlowPanel();
        artifactPanel.setStyleName("artifact-panel");
        artifactPanelBase.add(artifactPanel);

        initWidget(panel);
        //clear();
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void initArtifacts(WSearchResults o) {

        this.searchResults = o;
        clear();
        createBulkEditPanel();
        createNavigationPanel();

        groupPanels = new ArrayList<ArtifactGroupListPanel>();
        
        for (Iterator<EntryGroup> groups = o.getResults().iterator(); groups.hasNext();) {
            EntryGroup group = groups.next();

            ArtifactGroupListPanel list = new ArtifactGroupListPanel(group, isEditable());
            groupPanels.add(list);
            // get the list of artifacts from each item (artifact) in the group
            // and set them locally -- it's much easier to manipulate them this way

            SimplePanel rightTitlePanel = new SimplePanel();
            rightTitlePanel.setStyleName("right-title-panel");
            artifactPanel.add(rightTitlePanel);

            Label label = new Label(list.getTitle());
            label.setStyleName("right-title");
            rightTitlePanel.add(label);

            SimplePanel listContainer = new SimplePanel();
            listContainer.setStyleName("artifact-list-container");
            listContainer.add(list);

            artifactPanel.add(listContainer);
        }
    }

    public void clear() {
        artifactPanel.clear();
    }


    // each panel that links artifacts will have the option to
    // bulk edit all or some -- this handles the controls for that.
    private void createBulkEditPanel() {

        if (bulkEditPanel != null) {
            panel.remove(bulkEditPanel);
            bulkEditPanel = null;
        }

        long resultSize = searchResults.getTotal();

        if (resultSize > 0) {

            bulkEditPanel = new FlowPanel();
            bulkEditPanel.setStyleName("bulkedit-panel");

            //  we are in edit mode, offer new choices
            if (isEditable()) {

                // Cancel
                cancelLink = new Hyperlink();
                cancelLink.setText("Cancel");
                cancelLink.setTargetHistoryToken(History.getToken());
                cancelLink.addClickListener(this);
                Image cancelImg = new Image("images/page_deny.gif");
                cancelImg.setStyleName("icon-baseline");
                bulkEditPanel.add(asToolbarItem(cancelImg, cancelLink));

                // Edit entire result set
                editAll = new Hyperlink();
                editAll.setText("Edit All (" + resultSize + ")");
                editAll.setTargetHistoryToken("bulk-edit");
                editAll.addClickListener(this);
                Image editImage = new Image("images/page_right.gif");
                editImage.setStyleName("icon-baseline");
                bulkEditPanel.add(asToolbarItem(editImage, editAll));

                // Edit only the checked items
                editSelected = new Hyperlink();
                editSelected.setText("Edit Selected");
                editSelected.setTargetHistoryToken("bulk-edit");
                editSelected.addClickListener(this);
                Image editAllImg = new Image("images/page_tick.gif");
                editAllImg.setStyleName("icon-baseline");
                bulkEditPanel.add(asToolbarItem(editAllImg, editSelected, "bulkedit-toolbar-item-first"));

            } else {

                // Bulk edit link
                bulkEditLink = new Hyperlink();
                bulkEditLink.setText("Bulk Edit");
                bulkEditLink.setTargetHistoryToken(History.getToken());
                bulkEditLink.addClickListener(this);
                Image bulkImg = new Image("images/page_edit.gif");
                bulkImg.setStyleName("icon-baseline");
                bulkEditPanel.add(asToolbarItem(bulkImg, bulkEditLink, "bulkedit-toolbar-item-first"));
            }

            panel.insert(bulkEditPanel, 0);
        }
    }


    /**
     * Too many anonymous inner classes will create a listener object overhead.
     * This will allow a single listener to distinguish between multiple event publishers.
     *
     * @param sender
     */
    public void onClick(Widget sender) {

        // toggle edit mode
        if (sender == bulkEditLink) {
            setEditable(true);
            initArtifacts(searchResults);

            // edit only what the user selectes via the checkboxes
        } else if (sender == editSelected) {
            galaxy.createPageInfo("bulk-edit", new BulkEditPanel(getSelectedArtifacts(), galaxy), 0);
            History.newItem("bulk-edit");

            // edit the entire result set
            setEditable(false);
        } else if (sender == editAll) {
            galaxy.createPageInfo("bulk-edit", new BulkEditPanel(searchResults.getQuery(), searchResults.getTotal(), galaxy), 0);
            History.newItem("bulk-edit");

            // toggle edit mode
            setEditable(false);
        } else if (sender == cancelLink) {
            setEditable(false);
            initArtifacts(searchResults);
        }

    }


    private Widget asToolbarItem(Image img, Widget hl) {
        return asToolbarItem(img, hl, null);
    }

    private Widget asToolbarItem(Image img, Widget hl, String overrideStyle) {
        InlineFlowPanel p = asHorizontal(img, new Label(" "), hl);
        if (overrideStyle == null) {
            p.setStyleName("bulkedit-toolbar-item");
        } else {
            p.setStyleName(overrideStyle);
        }
        return p;
    }


    private void createNavigationPanel() {
        if (activityNavPanel != null) {
            panel.remove(activityNavPanel);
            activityNavPanel = null;
        }

        long resultSize = searchResults.getTotal();
        if (resultSize > maxResults || resultStart > 0) {
            activityNavPanel = new FlowPanel();
            activityNavPanel.setStyleName("activity-nav-panel");
            Hyperlink hl;

            if (resultSize > maxResults && resultStart < searchResults.getTotal()) {
                hl = new Hyperlink("Next", browsePanel.getHistoryToken() + "/" + (resultStart + maxResults));
                hl.setStyleName("activity-nav-next");
                hl.addClickListener(new ClickListener() {

                    public void onClick(Widget arg0) {
                        resultStart += maxResults;

                        browsePanel.refreshArtifacts(resultStart, maxResults);
                    }

                });
                activityNavPanel.add(hl);
            }

            if (resultStart > 0) {
                hl = new Hyperlink("Previous", browsePanel.getHistoryToken() + "/" + (resultStart - maxResults));
                hl.setStyleName("activity-nav-previous");
                hl.addClickListener(new ClickListener() {

                    public void onClick(Widget arg0) {
                        resultStart = resultStart - maxResults;
                        if (resultStart < 0) resultStart = 0;

                        browsePanel.refreshArtifacts(resultStart, maxResults);
                    }

                });
                activityNavPanel.add(hl);
            }

            SimplePanel spacer = new SimplePanel();
            spacer.add(new HTML("&nbsp;"));
            activityNavPanel.add(spacer);

            panel.insert(activityNavPanel, 0);
        }
    }

    // get the artifactIds from the selected checkboxes
    private Collection<String> getSelectedArtifacts() {
        Collection<String> artifactIds = new ArrayList<String>();
        for (ArtifactGroupListPanel list : groupPanels) {
            artifactIds.addAll(list.getSelectedEntries());
        }
        return artifactIds;
    }


    public int getResultStart() {
        return resultStart;
    }

    public void showLoadingMessage() {
        clear();
        artifactPanel.add(new Label("Loading..."));
    }

    public void setResultStart(int resultStart) {
        this.resultStart = resultStart;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
