package org.mule.galaxy.impl.upgrade;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.jackrabbit.util.ISO9075;

public class V153Upgrader extends AbstractSessionUpgrader {

    public V153Upgrader() {
        super("1.5.3", 6);
    }

    @Override
    protected void doUpgrade(Node root, QueryManager qm, Session session) throws RepositoryException {
        upgradeLifecycles(root, qm, session);
    }

    /**
     * Properly encode lifecycle node names
     */
    private void upgradeLifecycles(Node root, QueryManager qm, Session session) throws RepositoryException {
        Query q = qm.createQuery("/jcr:root/lifecycles/*", Query.XPATH);
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node l = itr.nextNode();

            String name = l.getName();
            String name2 = ISO9075.encode(name);
            
            if (!name.equals(name2)) {
                session.move(l.getPath(), l.getParent().getPath() + "/" + name2);
            }
        }
    }
    
}
