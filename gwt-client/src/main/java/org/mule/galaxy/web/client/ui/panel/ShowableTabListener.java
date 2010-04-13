/**
 * 
 */
package org.mule.galaxy.web.client.ui.panel;

import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.client.ui.panel.Showable;

import java.util.List;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;

public class ShowableTabListener implements Listener<TabPanelEvent> {
    private TabItem previous;
    private List<String> params;
    private List<String> previousParams;
    private final ErrorPanel errorPanel;
    private final List<String> tabNames;
    private final TabPanel tabPanel;
    private final String urlBase;
    
    public ShowableTabListener(TabPanel tabPanel,
                               ErrorPanel errorPanel, 
                               String urlBase,
                               List<String> params,
                               List<String> tabNames) {
        super();
        this.tabPanel = tabPanel;
        this.errorPanel = errorPanel;
        this.urlBase = urlBase;
        this.params = params;
        this.tabNames = tabNames;
    }


    /**
     * Update the parameters from a new history event. Call before calling showTab.
     * @param params
     */
    public void setParams(List<String> params) {
        this.params = params;
    }

    /**
     * Show a named tab. Handy when you have another page that manages history events and you have a tab
     * as a sub component which needs to be triggered on certain history events.
     * @param tabName
     */
    public void showTab(String tabName) {
        // call onHide() on what we were just showing
        hidePrevious();
        
        // find the next tab according to the URL token
        int idx = tabNames.indexOf(tabName);
        if (idx == -1) {
            idx = 0;
        }
        
        // and show it
        TabItem item = tabPanel.getItem(idx);
        Widget widget = item.getWidget(0);
        tabPanel.setSelection(item);
        if (widget instanceof Showable) {
            ((Showable)widget).showPage(params);
        }
        
        // Once we've shown a panel, store the previous params. We aren't going to trigger a new tab  
        // selection event again until we get new params.
        previousParams = params;
        previous = item;
    }

    private void hidePrevious() {
        if (previous != null) {
            Widget prevWidget = previous.getWidget(0);
            if (prevWidget instanceof Showable) {
                ((Showable) prevWidget).hidePage();
            }
        }
    }

    public void hidePage() {
        hidePrevious();
    }

    protected List<String> getTabNames() {
        return tabNames;
    }

    protected TabPanel getTabPanel() {
        return tabPanel;
    }

    public void handleEvent(TabPanelEvent event) {
        TabItem item = event.getItem();

        // Nasty trickery because GXT triggers tab selection events for every click on the screen
        if (item.equals(previous) && params.equals(previousParams)) {
            return;
        }

        hidePrevious();

        errorPanel.clearErrorMessage();
        Widget widget = item.getWidget(0);
        if (widget instanceof Showable) {
            ((Showable)widget).showPage(params);
        }

        // Update the History token once the tab is selected.
        int tabIndex = tabPanel.indexOf(item);
        if (tabNames != null && tabIndex < tabNames.size()) {
            String name = tabNames.get(tabIndex);
            History.newItem(urlBase + "/" + name, false);
        }

        previous = item;
        // Once we've shown a panel, store the previous params. We aren't going to trigger a new tab
        // selection event again until we get new params.
        previousParams = params;
        item.layout();
    }

    /**
     * @return tabName as used by History or null if not found
     */
    public String getTabName(TabItem item) {
        int idx = getTabPanel().indexOf(item);
        if (idx == -1) {
            return null;
        }
        return getTabNames().get(idx);
    }
}
