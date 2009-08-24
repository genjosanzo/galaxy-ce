package org.mule.galaxy.helloworld.server;

import org.mule.galaxy.helloworld.client.HelloWorldService;

public class HelloWorldServiceImpl implements HelloWorldService {

    public String sayHello(String name) {
        return "Hello " + name;
    }

}
