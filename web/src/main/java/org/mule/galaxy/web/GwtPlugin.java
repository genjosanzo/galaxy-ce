package org.mule.galaxy.web;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Map;

public class GwtPlugin {
    private String name;
    private String rootToken;
    private String moduleName;
    private Map<String,RemoteService> rpcServices;
    
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public void setRpcServices(Map<String,RemoteService> rpcServices) {
        this.rpcServices = rpcServices;
    }

    public Map<String,RemoteService> getRpcServices() {
        return rpcServices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootToken() {
        return rootToken;
    }

    public void setRootToken(String rootToken) {
        this.rootToken = rootToken;
    }
}
