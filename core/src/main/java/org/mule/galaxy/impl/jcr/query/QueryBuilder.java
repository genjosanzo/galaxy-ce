package org.mule.galaxy.impl.jcr.query;

import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public abstract class QueryBuilder {
    private String[] properties;
    private boolean artifactProperty;
    
    public QueryBuilder(boolean artifactProperty) {
        this(null, artifactProperty);
    }

    public QueryBuilder(String[] properties, boolean artifactProperty) {
        super();
        this.properties = properties;
        this.artifactProperty = artifactProperty;
    }

    public String[] getProperties() {
        return properties;
    }

    /**
     * Will this QueryBuilder be searching properties on the artifact itself?
     * Or on the artifact version node?
     * @return
     */
    public boolean isArtifactProperty() {
        return artifactProperty;
    }

    public abstract void build(StringBuilder query, 
                               String property, 
                               Object right, 
                               boolean not,
                               Operator operator) throws QueryException ;
}
