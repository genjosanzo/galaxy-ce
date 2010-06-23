package org.mule.galaxy.web.rpc;

import org.mule.galaxy.web.client.ui.panel.ErrorPanel;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * Display error messages every {@link AbstractLongRunningCallback#LONG_CALL_INTERVAL} milliseconds.
 * <br />
 * Provides visual feedback for long running call.
 * 
 * @see AbstractCallback#setErrorMessage(String, boolean)
 * @param <T>
 */
public class AbstractLongRunningCallback<T> extends AbstractCallback<T> {

    private final Timer longRunningCallTimer = new Timer() {
        @Override
        public void run() {
            setLongRunningErrorMessage();
        }
    };
    private Widget message;
    private static final int LONG_CALL_INTERVAL = 5000;

    public AbstractLongRunningCallback(final ErrorPanel panel) {
        super(panel);

        this.longRunningCallTimer.schedule(AbstractLongRunningCallback.LONG_CALL_INTERVAL);
    }

    public final void onSuccess(final T result) {
        this.longRunningCallTimer.cancel();
        removeMessage(message);
        onCallSuccess(result);
    }

    public void onCallSuccess(final T result) {
    }

    public final void onFailure(final Throwable caught) {
        this.longRunningCallTimer.cancel();
        removeMessage(message);
        onCallFailure(caught);
    }

    public void onCallFailure(final Throwable caught) {
    }

    private void setLongRunningErrorMessage() {
        message = setErrorMessage("Server is taking longer to respond than normal...");
    }
}
