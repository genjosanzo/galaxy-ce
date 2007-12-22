package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.HashMap;
import java.util.Map;

public class BasicArtifactInfo implements IsSerializable {
    private Map col2Value = new HashMap();
    
    public void setColumn(int col, String value) {
        col2Value.put(new Integer(col), value);
    }
    
    public String getValue(int col) {
        return (String)  col2Value.get(new Integer(col));
    }
}
