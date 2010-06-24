package org.mule.galaxy.web.client.ui.panel;

import java.util.Collection;
import java.util.LinkedList;

import org.mule.galaxy.web.rpc.AbstractCallback;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.user.client.Timer;
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
    private Timer autoHideErrorMessageTimer = new Timer() {
        @Override
        public void run() {
            removeAllMessages();
            fadeOut();
        }
    };
    private static final int AUTO_HIDE_DELAY = 4000;

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
                autoHideErrorMessageTimer.cancel();//It is safe to cancel a non-running timer
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
        addMessage(widget, false);
    }

    /**
    *
    * Display an error message. Will not be auto hidden.
    *
    * @see ErrorPanel#setMessage(String)
    * @param message
    * @param autoHide if true error message will be cleared after {@value AbstractCallback#AUTO_HIDE_DELAY} milliseconds.
    */
    public void addMessage(final Widget widget, final boolean autoHide) {
        add(widget);
        layout();
        if (messages.isEmpty()) {
            fadeIn();
        }
        messages.add(widget);
        if (autoHide) {
            autoHideErrorMessageTimer.schedule(ErrorContentPanel.AUTO_HIDE_DELAY);
        }
    }
    
    public void removeAllMessages() {
        for (final Widget message : messages) {
            remove(message);
        }
        messages.clear();
    }

}