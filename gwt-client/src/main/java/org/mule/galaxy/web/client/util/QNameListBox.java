package org.mule.galaxy.web.client.util;

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

import org.mule.galaxy.web.client.AbstractComposite;

public class QNameListBox extends AbstractComposite {

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
        
        InlineFlowPanel addRow = asHorizontal(addDocTypeTB, addButton);
        addRow.setStyleName("qnameListBox-add-row");
        table.add(addRow);
        
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
