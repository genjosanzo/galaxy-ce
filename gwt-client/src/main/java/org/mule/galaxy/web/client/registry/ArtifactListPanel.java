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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.TooltipListener;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.WSearchResults;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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
    private Map<CheckBox, String> CBCollection;
    
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

        CBCollection = new HashMap<CheckBox, String>();
        
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

        SimplePanel listContainer = new SimplePanel();
        listContainer.setStyleName("artifact-list-container");

        artifactPanel.add(listContainer);
        
        FlexTable table = super.createRowTable();
        listContainer.add(table);
        
        // each group contains one to many artifacts of the same type (ie, mule 1 configs)
        // and each type will (probably) have a different number of data points
        int numCols = o.getColumns().size();

        // create the colum headers
        // the first column is blank on purpose as it's reserved for the checkbox
        Image clearPixel = new Image("images/clearpixel.gif");
        table.setWidget(0, 0, clearPixel);

        // first column is wider in edit mode to accomodate the checkbox
        String firstColWidth = (this.editable) ? "20" : "1";
        table.getFlexCellFormatter().setWidth(0, 0, firstColWidth);
        table.getFlexCellFormatter().setWidth(0, 1, "180");

        for (int i = 0; i < numCols; i++) {
            int cPos = i + 1;
            table.setText(0, cPos, o.getColumns().get(i));
            // set each subsequent column to 100
            if (i > 1) table.getFlexCellFormatter().setWidth(0, i, "125");
        }
        
        int row = 0;
        for (ItemInfo i : o.getRows()) {
            renderItem(i, table, clearPixel, numCols, row);
            row++;
        }
    }

    private void renderItem(ItemInfo info, 
                            FlexTable table, 
                            Image clearPixel, 
                            int numCols,
                            int row) {
        if (editable) {
            CheckBox checkbox = new CheckBox();
            checkbox.setName(info.getId());
            table.setWidget(row + 1, 0, checkbox);
            CBCollection.put(checkbox, info.getId());
        } else {
            // draw nothing, we are not in edit mode
            table.setWidget(0, 0, clearPixel);
        }

        // draw the rest of the colums
        for (int c = 0; c < numCols; c++) {
            int cPos = c + 1;

            // truncate to N characters and offer a tooltip of the full value
            String value = info.getValue(c);
            String Id = info.getId();
            int truncateTo = 25;
            
            // use a label so we truncate and then attach a tooltip
            Label lvalue = new Label(abbreviate(value, truncateTo));
            // only attache if needed
            if(value.length() > truncateTo ) {
                lvalue.addMouseListener(new TooltipListener(value, 5000));
            }

            // the first column is the artifact name (value) and that's a link
            if (c == 0) {
                Hyperlink hl = new Hyperlink(value, "artifact/" + Id);
                table.setWidget(row + 1, cPos, hl);
            } else {
                // each additional value is just regular ol' text
                table.setWidget(row + 1, cPos, lvalue);
            }
            table.getRowFormatter().setStyleName(row + 1, "artifactTableEntry");
        }
    }

    private String abbreviate(String s, int width) {
        if (s.length() > width) {
            s = s.substring(0, width) + "...";
        }
        return s;
    }

    private Collection<String> getSelectedArtifacts() {      
        List<String> ids = new ArrayList<String>();
        for (Map.Entry<CheckBox, String> e : CBCollection.entrySet()) {
            if (e.getKey().isChecked()) {
                ids.add(e.getValue());
            }
        }
        return ids;
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

        ClickListener cl = new ClickListener() {
            public void onClick(Widget sender) {
                Window.open(searchResults.getFeed(), null, "scrollbars=yes");
            }
        };
        
        Image img = new Image("images/feed-icon.png");
        img.setTitle("Feed");
        img.addClickListener(cl);
        img.setStyleName("icon-baseline");
        
        Hyperlink hl = new Hyperlink("Feed", galaxy.getCurrentToken());
        hl.addClickListener(cl);

        bulkEditPanel = new FlowPanel();
        bulkEditPanel.setStyleName("bulkedit-panel");

        if (resultSize > 0) {

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
                bulkEditPanel.add(asToolbarItem(img, hl));
                
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
        } else {
            bulkEditPanel.add(asToolbarItem(img, hl, "bulkedit-toolbar-item-first"));
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

            if (resultStart + maxResults < resultSize) {
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
