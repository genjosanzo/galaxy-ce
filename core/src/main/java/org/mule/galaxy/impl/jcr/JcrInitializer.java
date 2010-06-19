package org.mule.galaxy.impl.jcr;

import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.xml.NodeTypeReader;
import org.springmodules.jcr.SessionFactory;

public class JcrInitializer {

    private static final String NAMESPACE = "http://galaxy.mule.org";
    private Session openSession;
    private SessionFactory sessionFactory;
    
    public void initialize() throws Exception {
        // Keep a session open so the transient repository doesn't shutdown
        openSession = sessionFactory.getSession();
        
        Session session = sessionFactory.getSession();
        
        // UGH, Jackrabbit specific code
        javax.jcr.Workspace workspace = session.getWorkspace();
        try {
            workspace.getNamespaceRegistry().getPrefix(NAMESPACE);
        } catch (NamespaceException e) {
            workspace.getNamespaceRegistry().registerNamespace("galaxy", NAMESPACE);
        }

        NodeTypeDef[] nodeTypes = NodeTypeReader.read(getClass()
            .getResourceAsStream("/org/mule/galaxy/impl/jcr/nodeTypes.xml"));

        // Get the NodeTypeManager from the Workspace.
        // Note that it must be cast from the generic JCR NodeTypeManager to the
        // Jackrabbit-specific implementation.

        NodeTypeManagerImpl ntmgr = (NodeTypeManagerImpl)workspace.getNodeTypeManager();

        // Acquire the NodeTypeRegistry
        NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry();        

        // Loop through the prepared NodeTypeDefs
        for (NodeTypeDef ntd : nodeTypes) {
            // ...and register it
            if (!ntreg.isRegistered(ntd.getName())) {
                ntreg.registerNodeType(ntd);
            }
        }
//        ntreg.dump(System.out);
        
        // This would normally go in an upgrader, but this needs to happen before anything else in the system happens
        final Node workspaces = JcrUtil.getOrCreate(session.getRootNode(), "workspaces", "galaxy:noSiblings");
        String versionStr = JcrUtil.getStringOrNull(workspaces, RegistryInitializer.REPOSITORY_LAYOUT_VERSION);
        if (versionStr != null) {
            final int version = Integer.parseInt(versionStr);
            
            if (version < 101) {
                makeLockable(session.getRootNode().getNodes());
            }
            session.save();
        }
        session.logout();
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
        
    public void destroy() throws Exception {
        openSession.logout();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
}
