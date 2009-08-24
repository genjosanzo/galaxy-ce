package org.mule.galaxy.helloworld.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface HelloWorldService extends RemoteService  {
    String sayHello(String name);
}
