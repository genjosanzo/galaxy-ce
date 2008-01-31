package org.mule.galaxy.query;

import org.mule.galaxy.api.query.SearchResults;

import java.util.Set;

public class SearchResultsImpl implements SearchResults
{
    private long total;
    private Set<Object> results;
    public SearchResultsImpl(long total, Set<Object> results) {
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
