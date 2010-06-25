package org.mule.galaxy.web.client.ui.panel;

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
            el.setStyleAttribute("opacity", 1);
            el.dom.getStyle().setProperty("opacity", "");
            el.dom.getStyle().setProperty("filter", "");
            removeAll();
        }

    }

    /**
     *
     * 
     * Fixes some opacity issue.
     *
     */
    private static class FadeIn extends SingleStyleEffect {

        public FadeIn(El el) {
          super(el, "opacity", 0, 1);
        }

        @Override
        public void increase(double value) {
          el.setStyleAttribute("opacity", value);
        }

        public void onComplete() {
          el.dom.getStyle().setProperty("opacity", "1.0");
          el.setStyleAttribute("filter", "");
        }

        public void onStart() {
          el.setStyleAttribute("opacity", 0);
          el.setVisible(true);
        }

      }

    private Timer autoCloseTimer = new Timer() {
        @Override
        public void run() {
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
        closeButton
    .   addSelectionListener(new SelectionListener<IconButtonEvent>() {
            @Override
            public void componentSelected(final IconButtonEvent event) {
                fadeOut();
                cancelAutoCloseTimer();
            }
        });
    }

    protected final void cancelAutoCloseTimer() {
        autoCloseTimer.cancel();// It is safe to cancel a non-scheduled timer
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
        if (getItemCount() == 0) {
            fadeIn();
        }
        add(widget);
        layout();

        cancelAutoCloseTimer();// Cancel eventual previously scheduled timer.
        if (autoHide) {
            autoCloseTimer.schedule(ErrorContentPanel.AUTO_HIDE_DELAY);
        }
    }

    public void fadeIn() {
        setVisible(true);
        if (isRendered()) {
            Fx fx = new Fx(FxConfig.NONE);
            fx.run(new FadeIn(el()));
        }
    }

    public void fadeOut() {
        if (isRendered()) {
            Fx fx = new Fx(FxConfig.NONE);
            fx.run(new FadeOutAndRemoveMessages(el()));
        }
    }

    protected void close() {
        hide();
        removeAll();
    }

}