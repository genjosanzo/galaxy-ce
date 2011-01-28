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

package org.mule.galaxy.repository.client.item;

import java.util.List;

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.web.client.ui.panel.AbstractShowable;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ItemInfoPanel extends AbstractShowable {

    private HorizontalPanel topPanel;
    private VerticalPanel rightGroup;
    private VerticalPanel panel;
    
    public ItemInfoPanel(RepositoryMenuPanel menuPanel,
                         ItemInfo item, 
                         final ItemPanel artifactPanel, 
                         final List<String> callbackParams) {
        panel = new VerticalPanel();
        
        topPanel = new HorizontalPanel();
        topPanel.setStyleName("artifactTopPanel");
        
        panel.add(createTitle("Details"));
        panel.add(topPanel);

        FlexTable table = createColumnTable();
        
        final NameEditPanel nep = new NameEditPanel(menuPanel,
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
        /*
        if (item.isLocal()) {
            panel.add(newSpacer());
            
            panel.add(new EntryMetadataPanel(menuPanel.getRepositoryModule(), menuPanel, "Metadata", item, false));
            panel.add(newSpacer());
            
            initComments();
            panel.add(newSpacer());

        }*/
        initWidget(panel);
    }
}
