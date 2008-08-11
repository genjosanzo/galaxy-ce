package org.mule.galaxy.query;

import java.util.Set;

import org.mule.galaxy.Item;

public class SearchResults {
    private long total;
    private Set<? extends Item> results;
    public SearchResults(long total, Set<? extends Item> results) {
        super();
        this.total = total;
        this.results = results;
    }
    
    public long getTotal() {
        return total;
    }
    
    public Set<? extends Item> getResults() {
        return results;
    }
    
    
}
