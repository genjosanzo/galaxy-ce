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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.WidgetHelper;
import org.mule.galaxy.web.client.validation.ListBoxNotEmptyValidator;
import org.mule.galaxy.web.client.validation.Validator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;

public abstract class AbstractUserModifiableListBox extends WidgetHelper {

    private ListBox listBox;
    private Button rmButton;
    private Button addButton;
    private ValidatableTextBox textBox;

    public AbstractUserModifiableListBox(Collection list,
                                         Validator validator) {
        super();

        listBox = new ListBox();

        if (validator == null) {
            validator = new ListBoxNotEmptyValidator(listBox);
        }

        listBox.setVisibleItemCount(5);
        if (list != null) {
            for (Iterator itr = list.iterator(); itr.hasNext();) {
                String q = (String) itr.next();

                listBox.addItem(q);
            }
        }
        FlowPanel table = new FlowPanel();

        rmButton = new Button("Remove");
        rmButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int idx = listBox.getSelectedIndex();
                if (idx != -1) {
                    listBox.removeItem(idx);
                }
            }
        });
        table.add(asHorizontal(listBox, rmButton));

        InlineFlowPanel addPanel = new InlineFlowPanel();
        textBox = new ValidatableTextBox(validator);
        textBox.getTextBox().setVisibleLength(60);
        addPanel.add(textBox);
        addButton = new Button("Add");
        addButton.addClickHandler(createValidator(textBox.getTextBox()));
        addPanel.add(addButton);

        InlineFlowPanel addRow = asHorizontal(textBox, addButton);
        addRow.setStyleName("qnameListBox-add-row");
        table.add(addRow);

        initWidget(table);
    }

    private ClickHandler createValidator(final TextBox addDocTypeTB) {
        return new ClickHandler() {
            public void onClick(ClickEvent event) {
                String text = addDocTypeTB.getText();
                if (isValid(text)) {
                    listBox.addItem(text);
                    addDocTypeTB.setText("");
                }
            }
        };
    }

    protected abstract boolean isValid(String text);

    public Collection<String> getItems() {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < listBox.getItemCount(); i++) {
            items.add(listBox.getValue(i));
        }
        return items;
    }

    public boolean validate() {
        return textBox.validate();
    }

    public void setEnabled(boolean e) {
        addButton.setEnabled(e);
        rmButton.setEnabled(e);
    }


}
