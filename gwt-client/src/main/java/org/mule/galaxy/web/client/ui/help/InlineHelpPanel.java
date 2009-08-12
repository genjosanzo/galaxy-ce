package org.mule.galaxy.web.client.ui.help;

import com.extjs.gxt.ui.client.widget.ContentPanel;


/**
 * ContenPanel has base styles hard coded (funky stuff in onRender too) so it needed to be extended
 */
public class InlineHelpPanel extends ContentPanel {

    private String status;

    public InlineHelpPanel() {
        super();
        baseStyle = "help-panel-inline";
        setDeferHeight(false);

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
