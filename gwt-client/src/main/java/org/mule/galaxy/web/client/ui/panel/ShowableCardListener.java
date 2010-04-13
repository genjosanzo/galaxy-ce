/**
 *
 */
package org.mule.galaxy.web.client.ui.panel;

import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.client.ui.panel.Showable;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.core.client.GWT;

import java.util.List;

public class ShowableCardListener extends SelectionListener {
    private Component previous;
    private List<String> params;
    private List<String> previousParams;
    private final ErrorPanel errorPanel;
    private final List<String> cardNames;
    private final CardPanel cardPanel;
    private final String urlBase;

    public ShowableCardListener(CardPanel cardPanel,
                                ErrorPanel errorPanel,
                                String urlBase,
                                List<String> params,
                                List<String> cardNames) {
        super();
        this.cardPanel = cardPanel;
        this.errorPanel = errorPanel;
        this.urlBase = urlBase;
        this.params = params;
        this.cardNames = cardNames;
    }


    /**
     * Update the parameters from a new history event. Call before calling showTab.
     *
     * @param params
     */
    public void setParams(List<String> params) {
        this.params = params;
    }

    public void showCard(String cardName) {
        int idx = cardNames.indexOf(cardName);
        if (idx == -1) {
            idx = 0;
        }
        Component item = cardPanel.getItem(idx);
        cardPanel.setActiveItem(item);
        if (item instanceof Showable) {
            ((Showable) item).showPage(params);
        }

        // Once we've shown a panel, store the previous params. We aren't going to trigger a new tab
        // selection event again until we get new params.
        previousParams = params;
        previous = item;
    }


    @Override
    public void componentSelected(ComponentEvent componentEvent) {
        GWT.log("## Component Selected Event triggered", null);
    }
}