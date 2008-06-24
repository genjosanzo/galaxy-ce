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
import org.mule.galaxy.web.rpc.BasicArtifactInfo;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Lists a group of artifacts.
 */
public class ArtifactGroupListPanel
    extends AbstractComposite
{
    private ArtifactGroup group;

    public ArtifactGroupListPanel(final ArtifactGroup group) {
        super();
        this.group = group;
        
        FlexTable table = super.createRowTable();

        for (int i = 0; i < group.getColumns().size(); i++) {
            table.setText(0, i, (String) group.getColumns().get(i));
        }
        
        for (int i = 0; i < group.getRows().size(); i++) {
            final BasicArtifactInfo info = (BasicArtifactInfo) group.getRows().get(i);
            for (int c = 0; c < group.getColumns().size(); c++) {
                if (c == 0) {
                    Hyperlink hl = new Hyperlink(info.getValue(c), "artifact_" + info.getId());
//                    MenuPanelPageInfo page = new MenuPanelPageInfo(hl.getTargetHistoryToken(), registryPanel) {
//                        public AbstractComposite createInstance() {
//                            return new ArtifactPanel(registryPanel, info.getId());
//                        }
//                    };
//                    registryPanel.addPage(page);
                    
                    table.setWidget(i+1, c, hl);
                } else {
                    table.setText(i+1, c, info.getValue(c));
                }
                table.getRowFormatter().setStyleName(i+1, "artifactTableEntry");
            }
        }

        initWidget(table);
    }

    public String getTitle()
    {
        return group.getName();
    }
}
