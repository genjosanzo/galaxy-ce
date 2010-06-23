package org.mule.galaxy.web.client.ui.panel;

import java.util.Collection;
import java.util.LinkedList;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * Specialized {@link ContentPanel} for displaying error messages.
 * <br />
 * Can be closed.
 *
 */
public class ErrorContentPanel extends ContentPanel {

    private Collection<Widget> messages = new LinkedList<Widget>();

    public ErrorContentPanel() {
        this.baseStyle = "error-panel";

        setDeferHeight(false);
        setStyleName(baseStyle);
        final ToolButton closeButton = new ToolButton("x-tool-close");
        getHeader().addTool(closeButton);
        closeButton.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            @Override
            public void componentSelected(final IconButtonEvent event) {
                removeAllMessages();
                fadeOut();
            }
        });
    }

    public void fadeIn() {
        setVisible(true);
        el().fadeIn(FxConfig.NONE);
    }

    public void fadeOut() {
        el().fadeOut(FxConfig.NONE);
        setVisible(false);
    }

    public void addMessage(final Widget widget) {
        add(widget);
        layout();
        if (messages.isEmpty()) {
            fadeIn();
        }
        messages.add(widget);
    }

    public void removeAllMessages() {
        for (final Widget message : messages) {
            remove(message);
        }
        messages.clear();
    }

}