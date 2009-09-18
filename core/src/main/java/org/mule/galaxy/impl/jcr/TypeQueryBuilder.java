package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.impl.jcr.query.SimpleQueryBuilder;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

/**
 * A query builder for extensions. By extending this class its easy to support queries which query based on
 * complex values which are stored as properties - effectively allowing you to do joins. For instance, you
 * could query on user.name instead of simply a user id.
 */
public class TypeQueryBuilder extends SimpleQueryBuilder {

    private Dao<Type> typeDao;
    protected TypeManager typeManager;

    @Override
    public boolean build(StringBuilder query, 
                         String property, 
                         String propPrefix, 
                         Object right, 
                         boolean not,
                         Operator operator) throws QueryException {
        List<String> matches = getMatches(right, property, operator);

        if (matches.size() == 0) {
            return false;
        }

        if (not) {
            query.append("not(");
        }

        String searchProp = getProperty(property);
        if (matches.size() > 1) {
//            Collection<?> rightCol = (Collection<?>)right;
//            if (rightCol.size() > 0) {
                boolean first = true;
                for (String value : matches) {
                    if (value == null) {
                        continue;
                    }

                    if (first) {
                        query.append("(");
                        first = false;
                    } else {
                        query.append(" or ");
                    }

                    query.append(propPrefix).append("@").append(searchProp).append("='").append(value)
                        .append("'");
                }
                query.append(")");
//            } else {
//                return false;
//            }
        } else if (matches.size() == 1) {
            query.append(propPrefix).append("@").append(searchProp).append("='").append(matches.get(0))
                .append("'");
        }

        if (not) {
            query.append(")");
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    protected List<String> getMatches(Object o, String property, Operator operator) throws QueryException {
        property = property.substring(property.lastIndexOf('.') + 1);
        if (Operator.LIKE == operator) {
            List results = typeDao.find(property, "%" + o.toString() + "%");

            return asIds((List<Identifiable>)results);
        } else if (Operator.EQUALS == operator || Operator.IN == operator) {
            List results = typeDao.find(property, o.toString());

            return asIds((List<Identifiable>)results);
        }
        return Collections.emptyList();
    }

    protected List<String> asIds(Collection<? extends Identifiable> results) {
        ArrayList<String> ids = new ArrayList<String>();
        for (Identifiable result : results) {
            ids.add(result.getId());
        }

        return ids;
    }

    protected String getProperty(String property) {
        return "type";
    }
    
    @Override
    public Collection<String> getProperties() {
        List<String> props = new ArrayList<String>();
        props.add("type.id");
        props.add("type.name");
        return props;
    }
    

    public String getRoot() {
        return "";
    }
    

    public Dao<Type> getTypeDao() {
        return typeDao;
    }

    public void setTypeDao(Dao<Type> typeDao) {
        this.typeDao = typeDao;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }
}