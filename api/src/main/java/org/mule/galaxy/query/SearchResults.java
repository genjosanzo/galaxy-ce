package org.mule.galaxy.query;

import java.util.Set;

public class SearchResults {
    private long total;
    private Set<? extends Object> results;
    public SearchResults(long total, Set<? extends Object> results) {
        super();
        this.total = total;
        this.results = results;
    }
    
    public long getTotal() {
        return total;
    }
    
    public Set<? extends Object> getResults() {
        return results;
    }
    
    
}
