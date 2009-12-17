package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GalaxyServiceAsync {

    void getApplicationInfo(AsyncCallback<ApplicationInfo> callback);

}
