package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

public class ArtifactGroup implements IsSerializable {
    private String name;
    private List columns = new ArrayList();
    private List rows = new ArrayList();
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List getColumns() {
        return columns;
    }
    public List getRows() {
        return rows;
    }
    
}
