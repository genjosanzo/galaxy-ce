package org.mule.galaxy.impl.jcr.query;

import java.util.Collection;

import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public class SimpleQueryBuilder extends QueryBuilder {
    
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
            .append(property)
            .append(", '%")
            .append(right)
            .append("%')");
        } else if (operator.equals(Operator.IN)) {
            Collection<?> rightCol = (Collection<?>) right;
            if (rightCol.size() > 0) {
                boolean first = true;
                for (Object o : rightCol) {
                    String value = getValueAsString(o);
                    
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
                         .append(property)
                         .append("='")
                         .append(value)
                         .append("'");
                }
                query.append(")");
            }
        } else {
            query.append("@")
                .append(property)
                .append("='")
                .append(getValueAsString(right))
                .append("'");
        }
        
        if (not) {
            query.append(")");
        }
    }

    protected String getValueAsString(Object o) throws QueryException {
        return o.toString();
    }

}
