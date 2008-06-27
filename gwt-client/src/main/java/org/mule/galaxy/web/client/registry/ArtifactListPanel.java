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
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.WSearchResults;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

public class ArtifactListPanel extends AbstractComposite {

    private FlowPanel panel;
    private FlowPanel artifactPanel;
    private int resultStart = 0;
    // TODO make it a configurable parameter, maybe per-user?
    private int maxResults = 15;
    private final AbstractBrowsePanel browsePanel;
    private FlowPanel activityNavPanel;
    private FlowPanel bulkEditPanel;
    private boolean editable;

    public ArtifactListPanel(AbstractBrowsePanel browsePanel) {
        super();
        this.browsePanel = browsePanel;

        panel = new FlowPanel();

        SimplePanel artifactPanelBase = new SimplePanel();
        artifactPanelBase.setStyleName("artifact-panel-base");
        panel.add(artifactPanelBase);

        artifactPanel = new FlowPanel();
        artifactPanel.setStyleName("artifact-panel");
        artifactPanelBase.add(artifactPanel);

        initWidget(panel);

        clear();
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void initArtifacts(WSearchResults o) {
        clear();
        createBulkEditPanel(o);
        createNavigationPanel(o);

        for (Iterator groups = o.getResults().iterator(); groups.hasNext();) {
            ArtifactGroup group = (ArtifactGroup) groups.next();

            ArtifactGroupListPanel list = new ArtifactGroupListPanel(group, isEditable());

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


    // each panel that links artifacts will have the option
    // bulk edit all or some -- this handles the controls for that.
    private void createBulkEditPanel(final WSearchResults o) {

        if (bulkEditPanel != null) {
            panel.remove(bulkEditPanel);
            bulkEditPanel = null;
        }

        long resultSize = o.getTotal();

        if (resultSize > 0) {
            bulkEditPanel = new FlowPanel();
            bulkEditPanel.setStyleName("activity-bulkedit-panel");

            //  we are in edit mode, offer new choices
            if (isEditable()) {

                ClickListener cl = new ClickListener() {
                    public void onClick(Widget sender) {
                        // toggle edit mode
                        setEditable(false);
                        initArtifacts(o);
                    }
                };

                // Cancel
                Hyperlink h = new Hyperlink();
                h.setText("Cancel");
                h.addClickListener(cl);
                Image imgCancel = new Image("images/page_deny.gif");
                imgCancel.addClickListener(cl);
                bulkEditPanel.add(asToolbarItem(imgCancel, h));

                // Edit entire result set
                Hyperlink ha = new Hyperlink();
                ha.setText("Edit All (" + resultSize + ")");
                ha.addClickListener(cl);
                Image imgAll = new Image("images/page_right.gif");
                imgAll.addClickListener(cl);
                bulkEditPanel.add(asToolbarItem(imgAll, ha));

                // Edit only the checked items
                Hyperlink hc = new Hyperlink();
                hc.setText("Edit Selected");
                hc.addClickListener(cl);
                Image imgSelected = new Image("images/page_tick.gif");
                imgSelected.addClickListener(cl);
                bulkEditPanel.add(asToolbarItem(imgSelected, hc, "activity-bulkedit-item-first"));

            } else {

                ClickListener cl = new ClickListener() {
                    public void onClick(Widget sender) {
                        // toggle edit mode
                        setEditable(true);
                        initArtifacts(o);
                    }
                };

                Hyperlink h = new Hyperlink();
                h.setText("Bulk Edit");
                h.addClickListener(cl);
                Image img = new Image("images/page_edit.gif");
                img.addClickListener(cl);
                bulkEditPanel.add(asToolbarItem(img, h, "activity-bulkedit-item-first"));

            }

            panel.insert(bulkEditPanel, 0);
        }
    }


    private Widget asToolbarItem(Image img, Widget hl) {
        return asToolbarItem(img, hl, null);
    }

    private Widget asToolbarItem(Image img, Widget hl, String overrideStyle) {
        InlineFlowPanel p = asHorizontal(img, new Label(" "), hl);
        if (overrideStyle == null) {
            p.setStyleName("activity-bulkedit-item");
        } else {
            p.setStyleName(overrideStyle);
        }
        return p;
    }


    private void createNavigationPanel(WSearchResults o) {
        if (activityNavPanel != null) {
            panel.remove(activityNavPanel);
            activityNavPanel = null;
        }

        long resultSize = o.getTotal();
        if (resultSize > maxResults || resultStart > 0) {
            activityNavPanel = new FlowPanel();
            activityNavPanel.setStyleName("activity-nav-panel");
            Hyperlink hl;

            if (resultSize > maxResults && resultStart < o.getTotal()) {
                hl = new Hyperlink("Next", browsePanel.getHistoryToken() + "_" + (resultStart + maxResults));
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
                hl = new Hyperlink("Previous", browsePanel.getHistoryToken() + "_" + (resultStart - maxResults));
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
