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

package org.mule.galaxy.web.client.util;

import org.mule.galaxy.web.client.AbstractComposite;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractUserModifiableListBox extends AbstractComposite {

    private ListBox listBox;
    private Button rmButton;
    private Button addButton;

    public AbstractUserModifiableListBox(Collection list) {
        super();
        
        listBox = new ListBox();
        listBox.setVisibleItemCount(5);
        if (list != null) {
            for (Iterator itr = list.iterator(); itr.hasNext();) {
                String q = (String)itr.next();
                
                listBox.addItem(q);
            }
        }
        FlowPanel table = new FlowPanel();
        
        rmButton = new Button("Remove");
        rmButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                int idx = listBox.getSelectedIndex();
                if (idx != -1) {
                    listBox.removeItem(idx);
                }
            }
        });
        table.add(asHorizontal(listBox, rmButton));
        
        InlineFlowPanel addPanel = new InlineFlowPanel();
        final TextBox addDocTypeTB = new TextBox();
        addDocTypeTB.setVisibleLength(60);
        addPanel.add(addDocTypeTB);
        addButton = new Button("Add");
        addButton.addClickListener(createValidator(addDocTypeTB));
        addPanel.add(addButton);
        
        InlineFlowPanel addRow = asHorizontal(addDocTypeTB, addButton);
        addRow.setStyleName("qnameListBox-add-row");
        table.add(addRow);
        
        initWidget(table);
    }

    private ClickListener createValidator(final TextBox addDocTypeTB) {
        return new ClickListener() {
            public void onClick(Widget sender) {
                String text = addDocTypeTB.getText();
                if (isValid(text)) {
                    listBox.addItem(text);
                    addDocTypeTB.setText("");
                }
            }
        };
    }

    protected abstract boolean isValid(String text);
    
    public Collection getItems() {
        ArrayList items = new ArrayList();
        for (int i = 0; i < listBox.getItemCount(); i++) {
            items.add(listBox.getValue(i));
        }
        return items;
    }

    public void setEnabled(boolean e) {
        addButton.setEnabled(e);
        rmButton.setEnabled(e);
    }

}
