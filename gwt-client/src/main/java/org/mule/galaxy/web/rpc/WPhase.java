package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WPhase implements IsSerializable {
    private String id;
    private String name;
    private Collection nextPhases;
    
    public WPhase(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public WPhase() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection getNextPhases() {
        return nextPhases;
    }

    public void setNextPhases(Collection nextPhases) {
        this.nextPhases = nextPhases;
    }
    
    
}
