package org.mule.galaxy.impl.jcr.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public abstract class QueryBuilder {
    protected Collection<String> properties = new ArrayList<String>();
    
    public QueryBuilder() {
        this(null);
    }

    public QueryBuilder(String[] properties) {
        super();
        if (properties != null) {
            Collections.addAll(this.properties, properties);
        }
    }

    public Collection<String> getProperties() {
        return properties;
    }

    /**
     * Build a JCR query.  
     * @param query
     * @param property
     * @param right
     * @param propertyPrefix 
     * @param not
     * @param operator
     * @return False if there is no way that this query will match any entries/artifacts.
     * @throws QueryException
     */
    public abstract boolean build(StringBuilder query, 
                                  String property, 
                                  String propertyPrefix, 
                                  Object right, 
                                  boolean not,
                                  Operator operator) throws QueryException ;
}
