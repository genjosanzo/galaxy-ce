package org.mule.galaxy.web.client.plugin;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class AbstractGwtPlugin implements EntryPoint {
    protected String rootToken;
    protected String name;
    
    public AbstractGwtPlugin(String rootToken, String name) {
        super();
        this.rootToken = rootToken;
        this.name = name;
    }

    public void onModuleLoad() {
        register(rootToken, getClass().getName());
        GWT.log(name + " Plugin loaded", null);
    }

    /*
     * Unfortunately this doesn't work at the moment as GWT changes all the method
     * names, so by hard coding ::loadPlugin, we break things when we're out of hosted mode. 
     */
    public native void register(String token, String className)
    /*-{
        var plugin = this;
        var callback = function() {
           plugin["@" + className + "::loadPlugin()"]();
        }
        $wnd.registerPlugin(token, this, callback);
    }-*/;
    
    public void loadPlugin() {  
        try {
            RootPanel.get("plugin");
        } catch (Throwable t) {
            // for some reason, the first time this is called, it throws an exception
            // in hosted mode. However, second time works fine. We'll just ignore it
            // for now I guess...
        }
        RootPanel insertPoint = RootPanel.get("plugin");
        insertPoint = RootPanel.get("plugin");
        load(insertPoint);
    }
    
    public abstract void load(RootPanel insertPoint);
    
}
