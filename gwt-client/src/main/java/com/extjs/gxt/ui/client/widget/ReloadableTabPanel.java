package com.extjs.gxt.ui.client.widget;

import org.mule.galaxy.web.client.ui.panel.ShowableTabListener;

import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;

/**
 * A fixed tab panel supporting reloads of tabs which are already selected. Had to put it in
 * a GXT package because many methods in superclass are package-private :(
 */
public class ReloadableTabPanel extends TabPanel {

    private TabItem lastSelectedItem;

    @Override
    void onItemClick(TabItem item, ComponentEvent ce) {
        if (item == lastSelectedItem) {
            onLastSelectedItemClick(item, ce);
        } else {
            super.onItemClick(item, ce);
        }
    }
    
    protected void onLastSelectedItemClick(TabItem item, ComponentEvent ce) {
        GWT.log("Force reloading the tab " + item.getText(), null);
        final List<Listener<? extends BaseEvent>> listeners = getListeners(Events.Select);
        for (Listener<? extends BaseEvent> listener : listeners) {
            if (listener instanceof ShowableTabListener) {
                ShowableTabListener l = (ShowableTabListener) listener;
                String tabName = l.getTabName(item);
                l.showTab(tabName);
            }
        }
    }

    @Override
    public void setSelection(TabItem item) {
        setLastSelectedItem(item);
        super.setSelection(item);
    }

    public TabItem getLastSelectedItem() {
        return lastSelectedItem;
    }

    public void setLastSelectedItem(TabItem lastSelectedItem) {
        this.lastSelectedItem = lastSelectedItem;
    }
}
