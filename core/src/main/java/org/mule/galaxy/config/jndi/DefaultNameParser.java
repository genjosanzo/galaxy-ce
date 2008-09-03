package org.mule.galaxy.config.jndi;

import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

/**
 * A default implementation of {@link javax.naming.NameParser}
 *
 */
public class DefaultNameParser implements NameParser {

    public Name parse(String name) throws NamingException {
        return new CompositeName(name);
    }
}