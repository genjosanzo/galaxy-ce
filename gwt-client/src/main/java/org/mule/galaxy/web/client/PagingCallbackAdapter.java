package org.mule.galaxy.web.client;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class PagingCallbackAdapter<T> implements AsyncCallback<List<T>> {
    private final AsyncCallback<PagingLoadResult<T>> callback;

    public PagingCallbackAdapter(AsyncCallback<PagingLoadResult<T>> callback) {
        this.callback = callback;
    }

    public void onFailure(Throwable arg0) {
        callback.onFailure(arg0);
    }

    public void onSuccess(List<T> arg0) {
        callback.onSuccess(new BasePagingLoadResult<T>(arg0));
    }
}
