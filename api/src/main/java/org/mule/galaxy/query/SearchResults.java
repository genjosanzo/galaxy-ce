package org.mule.galaxy.query;

import java.util.Set;

import org.mule.galaxy.Item;

public class SearchResults {
    private long total;
    private Set<Item> results;
    
    public SearchResults(long total, Set<Item> results) {
        super();
        this.total = total;
        this.results = results;
    }
    
    public long getTotal() {
        return total;
    }
    
    public Set<Item> getResults() {
        return results;
    }
}
