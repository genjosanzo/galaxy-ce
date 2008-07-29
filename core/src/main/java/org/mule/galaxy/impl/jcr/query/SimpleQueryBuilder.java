package org.mule.galaxy.impl.jcr.query;

import java.util.Collection;

import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public class SimpleQueryBuilder extends QueryBuilder {
    
    
    public SimpleQueryBuilder(boolean artifactProperty) {
	super(artifactProperty);
    }

    public SimpleQueryBuilder(String[] properties, boolean artifactProperty) {
        super(properties, artifactProperty);
    }

    public void build(StringBuilder query, String property, Object right, boolean not, Operator operator)
        throws QueryException {
        if (not) {
            query.append("not(");
        }
        
        if (operator.equals(Operator.LIKE)) {
            query.append("jcr:like(@")
            .append(getProperty(property))
            .append(", '%")
            .append(right)
            .append("%')");
        } else if (operator.equals(Operator.IN)) {
            Collection<?> rightCol = (Collection<?>) right;
            if (rightCol.size() > 0) {
                boolean first = true;
                for (Object o : rightCol) {
                    String value = getValueAsString(o, property, operator);
                    
                    if (value == null) {
                        continue;
                    }
                    
                    if (first) {
                        query.append("(");
                        first = false;
                    } else {
                        query.append(" or ");
                    }
    
                    query.append("@")
                         .append(getProperty(property))
                         .append("='")
                         .append(value)
                         .append("'");
                }
                query.append(")");
            }
        } else {
            query.append("@")
                .append(getProperty(property))
                .append("='")
                .append(getValueAsString(right, property, operator))
                .append("'");
        }
        
        if (not) {
            query.append(")");
        }
    }

    protected String getProperty(String property) {
        return property;
    }

    protected String getValueAsString(Object o, String property, Operator operator) throws QueryException {
        return o.toString();
    }

}
