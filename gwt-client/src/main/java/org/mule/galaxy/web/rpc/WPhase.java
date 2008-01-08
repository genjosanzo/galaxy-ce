package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WPhase implements IsSerializable {
    private String name;

    public WPhase(String name) {
        super();
        this.name = name;
    }

    public WPhase() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
