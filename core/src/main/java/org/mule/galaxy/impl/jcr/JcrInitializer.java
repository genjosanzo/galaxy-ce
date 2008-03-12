package org.mule.galaxy.impl.jcr;

import javax.jcr.NamespaceException;
import javax.jcr.Session;

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

        session.logout();
    }
    
    public void destroy() throws Exception {
        openSession.logout();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
}
