package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ColumnView
    extends Composite
{
    private FlowPanel panel;
    private FlowPanel container;
    private TreeListener treeListener;
    private TreeItem selectedItem;

    public ColumnView() {
        // The outer div. This gives us the scroll bar and the border.
        panel = new FlowPanel();
        panel.setStyleName("column-view");
        
        // The inner div. We adjust the width of this div to make the
        // columns all show up on one line.
        container = new FlowPanel();
        container.setStyleName("column-view-inner");
        panel.add(container);
        
        initWidget(panel);
    }

    public void setRootItem(TreeItem root, TreeItem selected)
    {
        container.clear();
        
        List selections = new ArrayList();
        TreeItem parent = selected;
        while (parent != null) {
            selections.add(0, parent);
            parent = parent.getParentItem();
        }
        
        addColumnWithItem(root, selections);
        
        
//        // Set the initial selection
//        FlowPanel column = (FlowPanel)container.getWidget(0);
//        Widget first  = (Widget)column.getWidget(0);
//        selectItemInColumn(root.getChild(0), first, column);
    }

    private void addColumnWithItem(TreeItem item, List selections) {
        // Create the column and add it to the container
        final FlowPanel column = new FlowPanel();
        column.setStyleName("column");
        container.add(column);
        
        // Be sure to adjust the container width:
        // The canvas has to be big enough to fit all the columns on one line
        int count = container.getWidgetCount();
        if (count <= 4)
            container.setWidth("603px");
        else
            // The columns are all 150px + a 1px border, except the first
            // column, which doesn't have a border.
            container.setWidth((count*151 - 1) + "px");
        
        // Add Hyperlinks for all the children of this item
        for (int idx=0; idx < item.getChildCount(); idx++) {
            final TreeItem child  = item.getChild(idx);
            
            Hyperlink link = new Hyperlink(child.getText(), "browse/" + child.getUserObject());
            column.add(link);
            link.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    selectItemInColumn(child, w, column, null, true);
                }
            });
            
            if (selections != null && selections.contains(child)) {
                selectItemInColumn(child, link, column, selections, false);
            }
        }
    }

    private void selectItemInColumn(TreeItem treeItem, Widget link, FlowPanel column, List selections, boolean fire) {
        // 1. Remove columns to the right of the column we're looking at
        int idx = 1 + container.getWidgetIndex(column);
        while (container.getWidgetCount() != idx)
            container.remove(idx);
        
        // 2. Change the visible selection
        // First, find and remove the old selection.
        for (Iterator iter = column.iterator(); iter.hasNext();) {
            Widget l = (Widget)iter.next();
            l.removeStyleName("selected");
        }
        // Then add the new selection
        link.addStyleName("selected");
        selectedItem = treeItem;
        
        // 3. Add the new column
        addColumnWithItem(treeItem, selections);
        
        // 4. Fire the "itemSelected" event
        if (treeListener != null && fire)
            treeListener.onTreeItemSelected(treeItem);
    }

    public void addTreeListener(TreeListener treeListener) {
        this.treeListener = treeListener;
    }
    
    public TreeItem getSelectedItem() {
        return selectedItem;
    }
}
