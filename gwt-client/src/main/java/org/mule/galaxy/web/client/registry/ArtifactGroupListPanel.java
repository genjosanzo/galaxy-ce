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

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.util.TooltipListener;
import org.mule.galaxy.web.rpc.EntryGroup;
import org.mule.galaxy.web.rpc.EntryInfo;

/**
 * Lists a group of artifacts.
 */
public class ArtifactGroupListPanel extends AbstractComposite {

    private EntryGroup group;
    private boolean editable;
    private Map<CheckBox, String> CBCollection;


    public ArtifactGroupListPanel(final EntryGroup group, boolean editable) {
        this.group = group;
        this.editable = editable;
        CBCollection = new HashMap<CheckBox, String>();
        renderArtifacts();
    }

    private void renderArtifacts() {
        FlexTable table = super.createRowTable();

        // each group contains one to many artifacts of the same type (ie, mule 1 configs)
        // and each type will (probably) have a different number of data points
        int numCols = group.getColumns().size();
        int numRows = group.getRows().size();

        // create the colum headers
        // the first column is blank on purpose as it's reserved for the checkbox
        table.setWidget(0, 0, new Image("images/clearpixel.gif"));

        // hardcode the width for the checkbox -- do this in css later
        table.getFlexCellFormatter().setWidth(0, 0, "20");
        table.getFlexCellFormatter().setWidth(0, 1, "200");

        for (int i = 0; i < numCols; i++) {
            int cPos = i + 1;
            table.setText(0, cPos, group.getColumns().get(i));
            // set each subsequent column to 100
            if (i > 1) table.getFlexCellFormatter().setWidth(0, i, "125");
        }

        // draw the rows for each artifact type in the group
        for (int i = 0; i < numRows; i++) {
            final EntryInfo info = group.getRows().get(i);

            // draw the checkbox in edit mode
            if (editable) {
                CheckBox checkbox = new CheckBox();
                checkbox.setName(info.getId());
                table.setWidget(i + 1, 0, checkbox);
                CBCollection.put(checkbox, info.getId());
            } else {
                // draw nothing, we are not in edit mode
                table.setText(0, 0, " ");
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
                    table.setWidget(i + 1, cPos, hl);
                } else {
                    // each additional value is just regular ol' text
                    table.setWidget(i + 1, cPos, lvalue);
                }
                table.getRowFormatter().setStyleName(i + 1, "artifactTableEntry");
            }
        }
        initWidget(table);
    }

    public String getTitle() {
        return group.getName();
    }

    private String abbreviate(String s, int width) {
        if (s.length() > width) {
            s = s.substring(0, width) + "...";
        }
        return s;
    }

    public Collection<String> getSelectedEntries() {            
        List<String> ids = new ArrayList<String>();
        for (Map.Entry<CheckBox, String> e : CBCollection.entrySet()) {
            if (e.getKey().isChecked()) {
                ids.add(e.getValue());
            }
        }
        return ids;
    }

}
