package org.mule.galaxy.impl.jcr.query;

import java.util.Collection;

import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.query.QueryException;

public class SimpleQueryBuilder extends QueryBuilder {
    
    
    public SimpleQueryBuilder() {
    super();
    }

    public SimpleQueryBuilder(String[] properties) {
        super(properties);
    }

    public boolean build(StringBuilder query, String property, String propPrefix,
                         Object right, boolean not, Operator operator)
        throws QueryException {
        if (not) {
            query.append("not(");
        }
        
        if (operator.equals(Operator.LIKE)) {
            query.append("jcr:like(")
            .append(propPrefix)
            .append("@")
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
    
                    query.append(propPrefix)
                    .append("@")
                         .append(getProperty(property))
                         .append("='")
                         .append(value)
                         .append("'");
                }
                query.append(")");
            } else {
                return false;
            }
        } else {
            query.append(propPrefix)
                .append("@")
                .append(getProperty(property))
                .append("='")
                .append(getValueAsString(right, property, operator))
                .append("'");
        }
        
        if (not) {
            query.append(")");
        }
        
        return true;
    }

    protected String getProperty(String property) {
        return property;
    }

    protected String getValueAsString(Object o, String property, Operator operator) throws QueryException {
        return o.toString();
    }
}
