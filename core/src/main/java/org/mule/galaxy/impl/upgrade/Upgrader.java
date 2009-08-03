package org.mule.galaxy.impl.upgrade;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public abstract class Upgrader {
    /**
     * 
     * @param version The version of the Galaxy JCR structure that is being upgraded to.
     * @param session
     * @param root The root of the JCR tree. This may not in fact be the real root node 
     * as we may import nodes into a subnode.
     * @throws RepositoryException
     * @throws Exception 
     */
    public abstract void doUpgrade(int version, Session session, Node root) throws Exception;
}
