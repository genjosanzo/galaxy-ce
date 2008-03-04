package org.mule.galaxy.web.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QNameListBox extends Composite {

    private ListBox listBox;
    private Button rmButton;
    private Button addButton;

    public QNameListBox(Collection list) {
        super();
        
        listBox = new ListBox();
        listBox.setVisibleItemCount(5);
        if (list != null) {
            for (Iterator itr = list.iterator(); itr.hasNext();) {
                String q = (String)itr.next();
                
                listBox.addItem(q);
            }
        }
        FlexTable table = new FlexTable();
        table.setWidget(0, 0, listBox);
        
        rmButton = new Button("Remove");
        rmButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                int idx = listBox.getSelectedIndex();
                if (idx != -1) {
                    listBox.removeItem(idx);
                }
            }
        });
        table.setWidget(0, 1, rmButton);
        
        InlineFlowPanel addPanel = new InlineFlowPanel();
        final TextBox addDocTypeTB = new TextBox();
        addDocTypeTB.setVisibleLength(60);
        addPanel.add(addDocTypeTB);
        addButton = new Button("Add");
        addButton.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                String text = addDocTypeTB.getText();
                
                if (!text.startsWith("{") || text.indexOf('{', 1) != -1) {
                    Window.alert("Document type QNames must be in the form of \"{NAMESPACE}LOCAL-NAME\"");
                    return;
                }
                int rightIdx = text.indexOf("}");
                if (rightIdx != -1) {
                    if (text.indexOf('}', rightIdx+1) != -1) {
                        Window.alert("Document type QNames must be in the form of \"{NAMESPACE}LOCAL-NAME\"");
                        return;
                    }
                } else {
                    Window.alert("Document type QNames must be in the form of \"{NAMESPACE}LOCAL-NAME\"");
                    return;
                }
                
                listBox.addItem(text);
                addDocTypeTB.setText("");
            }
            
        });
        addPanel.add(addButton);
        
        table.setWidget(1, 0, addPanel);
        table.getFlexCellFormatter().setRowSpan(1, 0, 2);
        
        initWidget(table);
    }
    
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
