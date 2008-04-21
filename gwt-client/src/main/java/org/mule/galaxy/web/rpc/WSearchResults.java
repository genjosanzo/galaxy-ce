package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WSearchResults implements IsSerializable {
    private long total;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.ArtifactGroup>
     */
    private Collection results;
    
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public Collection getResults() {
        return results;
    }
    public void setResults(Collection results) {
        this.results = results;
    }
}
