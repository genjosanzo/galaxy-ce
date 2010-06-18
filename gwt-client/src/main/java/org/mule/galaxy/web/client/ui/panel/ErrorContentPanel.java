package org.mule.galaxy.web.client.ui.panel;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;

/**
 *
 * Specialized {@link ContentPanel} for displaying error messages.
 * <br />
 * Can be closed.
 *
 */
public class ErrorContentPanel extends ContentPanel {

    public ErrorContentPanel() {
        this.baseStyle = "error-panel";

        setDeferHeight(false);
        setStyleName(baseStyle);
        ToolButton btn = new ToolButton("x-tool-close");
        getHeader().addTool(btn);
        btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            @Override
            public void componentSelected(IconButtonEvent ce) {
                el().fadeOut(FxConfig.NONE);
            }
        });
    }
}
