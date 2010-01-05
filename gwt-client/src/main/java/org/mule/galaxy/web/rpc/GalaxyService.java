package org.mule.galaxy.web.rpc;

import org.mule.galaxy.web.client.RPCException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface GalaxyService extends RemoteService {

    ApplicationInfo getApplicationInfo() throws RPCException;

}
