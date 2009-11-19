package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PluginTabInfo implements IsSerializable {
    private String name;
    private boolean isTab;
    private String rootToken;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isTab() {
        return isTab;
    }
    public void setTab(boolean isTab) {
        this.isTab = isTab;
    }
    public String getRootToken() {
        return rootToken;
    }
    public void setToken(String rootToken) {
        this.rootToken = rootToken;
    }
    
}
