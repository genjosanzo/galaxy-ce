package org.mule.galaxy.impl.link;

import java.beans.IntrospectionException;

import org.mule.galaxy.impl.extension.IdentifiableExtension;
import org.mule.galaxy.impl.extension.IdentifiableExtensionQueryBuilder;

public class LinkExtensionQueryBuilder extends IdentifiableExtensionQueryBuilder {

    public LinkExtensionQueryBuilder(IdentifiableExtension e) throws IntrospectionException {
        super(e);
        
        getSuffixes().add("");
        getSuffixes().add("reciprocal");
    }

}
