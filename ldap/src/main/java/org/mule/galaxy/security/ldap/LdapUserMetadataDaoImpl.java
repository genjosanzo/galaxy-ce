package org.mule.galaxy.security.ldap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class LdapUserMetadataDaoImpl extends AbstractReflectionDao<LdapUserMetadata> {
    public LdapUserMetadataDaoImpl() throws Exception {
        super(LdapUserMetadata.class, "ldapUserMetadata", false);
    }

    @Override
    protected String generateNodeName(LdapUserMetadata t) {
        return t.getId();
    }

    @Override
    protected String getObjectNodeName(LdapUserMetadata t) {
        return t.getId();
    }

    protected Node findNode(String id, Session session) throws RepositoryException {
        try {
            return getObjectsNode(session).getNode(id);
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    @Override
    protected void doSave(LdapUserMetadata t, Session session) throws RepositoryException, NotFoundException {
        super.doSave(t, session);
    }
    
}
