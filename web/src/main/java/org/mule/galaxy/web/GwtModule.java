package org.mule.galaxy.web;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;

public class GwtModule {
	private boolean core;
    private String name;
    private Map<String,RemoteService> rpcServices;
    
    public String getName() {
        return name;
    }

    public void setName(String moduleName) {
        this.name = moduleName;
    }
    
    public void setRpcServices(Map<String,RemoteService> rpcServices) {
        this.rpcServices = rpcServices;
    }

    public Map<String,RemoteService> getRpcServices() {
        return rpcServices;
    }

	public boolean isCore() {
		return core;
	}

	public void setCore(boolean core) {
		this.core = core;
	}
    
}
