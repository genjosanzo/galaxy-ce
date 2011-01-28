package org.mule.galaxy.impl.artifact;

import java.beans.IntrospectionException;
import java.util.Collections;
import java.util.List;

import org.mule.galaxy.impl.extension.ExtensionQueryBuilder;
import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class ArtifactQueryBuilder extends ExtensionQueryBuilder {
    
    public ArtifactQueryBuilder(ArtifactExtension e) throws IntrospectionException {
        this.extension = e;
        
        getSuffixes().add("contentType");
        getSuffixes().add("documentType");
    }

    @Override
    public boolean build(StringBuilder query, String property, String propPrefix, Object right, boolean not,
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
    
    protected List<String> getMatches(Object o, String property, Operator operator) throws QueryException {
        boolean contentType = false;
        if (property.endsWith(".contentType")) {
            property = property.substring(0, property.lastIndexOf('.'));
            contentType = true;
        } else if (property.endsWith(".documentType")) {
            property = property.substring(0, property.lastIndexOf('.'));
        } else {
            throw new QueryException(new Message("Invalid property " + property, BundleUtils.getBundle(getClass())));
        }
        
        PropertyDescriptor pd = typeManager.getPropertyDescriptorByName(property);
        
        if (pd == null) {
            return Collections.emptyList();
        }
        
        if (contentType) {
            return ((ArtifactExtension) extension).getArtifactsForContentType(pd.getId(), operator == Operator.LIKE, o);
        } else {
            return ((ArtifactExtension) extension).getArtifactsForDocumentType(pd.getId(), operator == Operator.LIKE, o);
        }
    }

    @Override
    protected String getProperty(String property) {
        return property.substring(0, property.lastIndexOf('.'));
    }

}
