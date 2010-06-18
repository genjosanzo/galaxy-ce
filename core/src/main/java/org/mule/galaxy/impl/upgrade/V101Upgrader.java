package org.mule.galaxy.impl.upgrade;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.QueryManager;
import javax.jcr.version.VersionException;

/**
 * Makes all nodes lockable.
 */
public class V101Upgrader extends AbstractSessionUpgrader {

    public V101Upgrader() {
        super("101", 101);
    }

    @Override
    protected void doUpgrade(Node root, QueryManager qm, Session session) throws RepositoryException {
        makeLockable(session.getRootNode().getNodes());
    }

    private void makeLockable(NodeIterator itr) throws RepositoryException, NoSuchNodeTypeException, VersionException,
            ConstraintViolationException, LockException {
        while (itr.hasNext()) {
            Node l = itr.nextNode();
            if (!l.getName().startsWith("jcr:")) {
                l.addMixin("mix:lockable");
                
                makeLockable(l.getNodes());
            }
        }
    }
    
}
