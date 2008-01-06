package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.HashMap;
import java.util.Map;

public class BasicArtifactInfo implements IsSerializable {
    /*
     * @gwt typeArgs java.lang.Integer,java.lang.String
     */
    private Map col2Value = new HashMap();
    private String id;
    private String workspaceId;
    
    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setColumn(int col, String value) {
        col2Value.put(new Integer(col), value);
    }
    
    public String getValue(int col) {
        return (String)  col2Value.get(new Integer(col));
    }
}
