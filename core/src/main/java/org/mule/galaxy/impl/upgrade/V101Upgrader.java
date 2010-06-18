package org.mule.galaxy.impl.upgrade;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;

/**
 * Makes all nodes lockable.
 */
public class V101Upgrader extends AbstractSessionUpgrader {

    public V101Upgrader() {
        super("101", 101);
    }

    @Override
    protected void doUpgrade(Node root, QueryManager qm, Session session) throws RepositoryException {
        // do nothing. Other products override this class to provide functionality.
    }
    
}
