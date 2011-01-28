package org.mule.galaxy.helloworld.client;

import org.mule.galaxy.web.client.plugin.AbstractGwtPlugin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class HelloWorldPlugin extends AbstractGwtPlugin {

    public HelloWorldPlugin() {
        super("helloworld", "HelloWorld");
    }
    
    public native void register(String token, String className)
    /*-{
        var plugin = this;
        var callback = function() {
           plugin.@org.mule.galaxy.helloworld.client.HelloWorldPlugin::loadPlugin()();
        }
        $wnd.registerPlugin(token, this, callback);
    }-*/;
    
    public void load(final RootPanel insertPoint) {
        insertPoint.add(new Label("Invoking Service..."));
        
        HelloWorldServiceAsync helloWorldSvc = (HelloWorldServiceAsync) GWT.create(HelloWorldService.class);

        ServiceDefTarget target = (ServiceDefTarget) helloWorldSvc;
        String baseUrl = GWT.getModuleBaseURL();
        target.setServiceEntryPoint(baseUrl + "../handler/hello.rpc");
        
        helloWorldSvc.sayHello("Mule", new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                GWT.log("Could not invoke hello world service.", t); 
            } 

            public void onSuccess(String resp) {
                insertPoint.add(new Label(resp));
            }
            
        });

    }
    
}
