package org.mule.galaxy.query;

import java.util.Set;

public class SearchResults {
    private long total;
    private Set<Object> results;
    public SearchResults(long total, Set<Object> results) {
        super();
        this.total = total;
        this.results = results;
    }
    
    public long getTotal() {
        return total;
    }
    
    public Set<Object> getResults() {
        return results;
    }
    
    
}
