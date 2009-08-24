package org.mule.galaxy.helloworld.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HelloWorldServiceAsync {
    void sayHello(String name, AsyncCallback<String> callback);
}
