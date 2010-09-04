package org.mule.galaxy;

import java.io.Serializable;
import java.util.List;

/**
 * Results from a search where the data is a subset or all of a specified search,
 * and total is the number of total results.
 */
public class Results<Type> implements Serializable {
    private List<Type> data;
    private long total;
    
    public Results() {
        super();
    }

    public Results(List<Type> data) {
        this(data, data.size());
    }

    public Results(List<Type> data, long total) {
        super();
        this.data = data;
        this.total = total;
    }
    
    public List<Type> getData() {
        return data;
    }
    public void setData(List<Type> data) {
        this.data = data;
    }
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
}
