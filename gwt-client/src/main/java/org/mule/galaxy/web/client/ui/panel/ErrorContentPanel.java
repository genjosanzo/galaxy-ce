package org.mule.galaxy.web.client.ui.panel;

import java.util.Collection;
import java.util.LinkedList;

import org.mule.galaxy.web.rpc.AbstractCallback;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.Fx;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.fx.SingleStyleEffect;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Specialized {@link ContentPanel} for displaying error messages. <br />
 * Can be closed.
 * 
 */
public class ErrorContentPanel extends ContentPanel {

    private Collection<Widget> messages = new LinkedList<Widget>();
    private Timer autoCloseTimer = new Timer() {
        @Override
        public void run() {
            close();
        }
    };
    private static final int AUTO_HIDE_DELAY = 4000;

    /**
     * 
     * Extends BaseEffect$FadeOut by removing messages after the fadeout. <br />
     * Because default FadeOut cannot be cleanly extended its code is
     * duplicated.
     * 
     */
    private class FadeOutAndRemoveMessages extends SingleStyleEffect {

        public FadeOutAndRemoveMessages(El el) {
            super(el, "opacity", 1, 0);
        }

        @Override
        public void increase(double value) {
            el.setStyleAttribute("opacity", Math.max(value, 0));
        }

        public void onComplete() {
            el.setVisible(false);
            el.dom.getStyle().setProperty("opacity", "");
            el.dom.getStyle().setProperty("filter", "");
            removeAllMessages();
        }

    }

    public ErrorContentPanel() {
        this.baseStyle = "error-panel";

        setDeferHeight(false);
        setStyleName(baseStyle);
        final ToolButton closeButton = new ToolButton("x-tool-close");
        getHeader().addTool(closeButton);
        closeButton
    .   addSelectionListener(new SelectionListener<IconButtonEvent>() {
            @Override
            public void componentSelected(final IconButtonEvent event) {
                close();
                autoCloseTimer.cancel();// It is safe to cancel a non-scheduled timer
            }
        });
    }

    public void fadeIn() {
        setVisible(true);
        if (isRendered()) {
            el().fadeIn(FxConfig.NONE);
        }
    }

    public void fadeOut() {
        if (isRendered()) {
            Fx fx = new Fx(FxConfig.NONE);
            fx.run(new FadeOutAndRemoveMessages(el()));
        }
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
     * @param autoHide
     *            if true error message will be cleared after
     *            {@value AbstractCallback#AUTO_HIDE_DELAY} milliseconds.
     */
    public void addMessage(final Widget widget, final boolean autoHide) {
        add(widget);
        layout();
        if (messages.isEmpty()) {
            fadeIn();
        }
        messages.add(widget);
        autoCloseTimer.cancel();// Cancel eventual previously scheduled timer.
        if (autoHide) {
            autoCloseTimer.schedule(ErrorContentPanel.AUTO_HIDE_DELAY);
        }
    }

    protected void removeAllMessages() {
        for (final Widget message : messages) {
            remove(message);
        }
        messages.clear();
    }

    public void close() {
        fadeOut();
    }

}