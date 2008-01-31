package org.mule.galaxy.test;

import org.mule.galaxy.api.ActivityManager;
import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactPolicyException;
import org.mule.galaxy.api.ArtifactResult;
import org.mule.galaxy.api.CommentManager;
import org.mule.galaxy.api.IndexManager;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.RegistryException;
import org.mule.galaxy.api.Settings;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.lifecycle.LifecycleManager;
import org.mule.galaxy.api.policy.PolicyManager;
import org.mule.galaxy.api.security.User;
import org.mule.galaxy.api.security.UserManager;
import org.mule.galaxy.impl.IndexManagerImpl;
import org.mule.galaxy.impl.jcr.PluginRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springmodules.jcr.SessionFactory;
import org.springmodules.jcr.SessionFactoryUtils;

public class AbstractGalaxyTest extends AbstractDependencyInjectionSpringContextTests {

    protected static final Log log = LogFactory.getLog(AbstractGalaxyTest.class);

    protected JackrabbitRepository repository;
    protected Registry registry;
    protected Settings settings;
    protected SessionFactory sessionFactory;
    protected LifecycleManager lifecycleManager;
    protected UserManager userManager;
    protected Session session;
    protected PolicyManager policyManager;
    protected IndexManager indexManager;
    protected ActivityManager activityManager;
    protected CommentManager commentManager;
    protected PluginRunner pluginRunner;
    
    private boolean participate;
    
    public AbstractGalaxyTest() {
        super();
        setPopulateProtectedVariables(true);
    }

    public URL getResource(String name) {
        URL url = getClass().getResource(name);
        assertNotNull("Resource not found: " + name, url);

        return url;
    }

    public InputStream getResourceAsStream(String name) {
        InputStream is = getClass().getResourceAsStream(name);
        assertNotNull("Resource not found: " + name, is);

        return is;
    }

    protected User getAdmin() {
        return userManager.authenticate("admin", "admin");
    }
    
    protected Artifact importHelloWsdl() 
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace,
                                                    "application/xml", 
                                                    "hello_world.wsdl", 
                                                    "0.1", 
                                                    helloWsdl, 
                                                    getAdmin());
        return ar.getArtifact();
    }

    protected Artifact importHelloMule2() throws RegistryException, ArtifactPolicyException, IOException,
        MimeTypeParseException {
        InputStream helloWsdl = getResourceAsStream("/mule2/hello-config.xml");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace, 
                                                    "application/xml", 
                                                    "hello-config.xml", 
                                                    "0.1", helloWsdl, getAdmin());
        return ar.getArtifact();
    }

    protected Artifact importHelloMule() throws RegistryException, ArtifactPolicyException, IOException,
        MimeTypeParseException {
        InputStream helloWsdl = getResourceAsStream("/mule/hello-config.xml");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactResult ar = registry.createArtifact(workspace,
                                                    "application/xml",
                                                    "hello-config.xml",
                                                    "0.1", helloWsdl, getAdmin());
        return ar.getArtifact();
    }
    
    private void clearJcrRepository() {
        try {
            Session session = repository.login(new SimpleCredentials("username", "password".toCharArray()));

            Node node = session.getRootNode();
//            JcrUtil.dump(node.getNode("activities"));
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                Node child = itr.nextNode();
                if (!child.getName().startsWith("jcr:")) {
                    child.remove();
                }
            }
            session.save();
            session.logout();
        } catch (PathNotFoundException t) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml"
        };
    }

    @Override  
    protected void onSetUp() throws Exception {
        super.onSetUp();
        
        Session session = null;
        participate = false;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // Do not modify the Session: just set the participate
            // flag.
            participate = true;
        } else {
            logger.debug("Opening reeindexing session");
            session = SessionFactoryUtils.getSession(sessionFactory, true);
            TransactionSynchronizationManager.bindResource(sessionFactory, sessionFactory.getSessionHolder(session));
        }

    }

    @Override
    protected void onTearDown() throws Exception {
        ((IndexManagerImpl) applicationContext.getBean("indexManagerTarget")).destroy();

        if (repository != null) {
            clearJcrRepository();
            setDirty();
        }

        if (!participate) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
            logger.debug("Closing reindexing session");
            SessionFactoryUtils.releaseSession(session, sessionFactory);
        }
        super.onTearDown();
    }


}
