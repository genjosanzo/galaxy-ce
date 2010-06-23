package org.mule.galaxy.web.client.ui.util;

import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.rpc.AbstractLongRunningCallback;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StartBackgroundRefreshCallback<T> extends AbstractLongRunningCallback<T> {

    private final AsyncCallback<T> callback;
    private Timer timer;
    private final int delay;

    public StartBackgroundRefreshCallback(ErrorPanel errorPanel, Timer timer, AsyncCallback<T> callback) {
        this(errorPanel, timer, 10000, callback);
    }
    
    public StartBackgroundRefreshCallback(ErrorPanel errorPanel, Timer timer, int delay, AsyncCallback<T> callback) {
        super(errorPanel);
        this.timer = timer;
        this.delay = delay;
        this.callback = callback;
    }
    
    @Override
    public void onCallFailure(Throwable t) {
        timer.schedule(delay);
        callback.onFailure(t);
    }

    @Override
    public void onCallSuccess(T r) {
        timer.schedule(delay);
        callback.onSuccess(r);
    }

}
