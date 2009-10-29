/**
 * 
 */
package org.mule.galaxy.web.client.util;

import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Showable;

public class ShowableTabListener extends SelectionListener<TabPanelEvent> {
    private TabItem previous;
    private List<String> params;
    private List<String> previousParams;
    private final ErrorPanel errorPanel;
    private final List<String> tabNames;
    private final TabPanel tabPanel;
    
    public ShowableTabListener(TabPanel tabPanel,
                               ErrorPanel errorPanel, 
                               List<String> params,
                               List<String> tabNames) {
        super();
        this.tabPanel = tabPanel;
        this.errorPanel = errorPanel;
        this.params = params;
        this.tabNames = tabNames;
    }

    @Override
    public void componentSelected(TabPanelEvent ce) {
        TabItem item = ce.getItem();
        
        if (item.equals(previous) && params.equals(previousParams)) {
            return;
        }
        
        if (previous != null) {
            Widget widget = previous.getWidget(0);
            if (widget instanceof Showable) {
                ((Showable)widget).hidePage();
            }
        }
        
        errorPanel.clearErrorMessage();
        Widget widget = item.getWidget(0);
        if (widget instanceof Showable) {
            ((Showable)widget).showPage(params);
        }
        
        previous = item;
        item.layout();
    }

    public void setParams(List<String> params) {
        this.previousParams = this.params;
        this.params = params;
    }

    public void showTab(String tabName) {
        int idx = tabNames.indexOf(tabName);
        if (idx == -1) {
            idx = 0;
        }
        TabItem item = tabPanel.getItem(idx);
        Widget widget = item.getWidget(0);
        tabPanel.setSelection(item);
        if (widget instanceof Showable) {
            ((Showable)widget).showPage(params);
        }
    }
}