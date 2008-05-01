package org.mule.galaxy.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.mule.galaxy.ActivityManager;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.CommentManager;
import org.mule.galaxy.PluginManager;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Settings;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.index.IndexManagerImpl;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springmodules.jcr.SessionFactory;
import org.springmodules.jcr.SessionFactoryUtils;

public abstract class AbstractGalaxyTest extends AbstractDependencyInjectionSpringContextTests {

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
    protected PluginManager pluginManager;
    protected AccessControlManager accessControlManager;
    
    private boolean participate;
    
    public AbstractGalaxyTest() {
        super();
        setPopulateProtectedVariables(true);
    }

    public URL getResource(String name) throws IOException
    {
        ResourceLoader loader = new DefaultResourceLoader();
        URL url = loader.getResource(name).getURL();
        assertNotNull("Resource not found: " + name, url);

        return url;
    }

    public InputStream getResourceAsStream(String name) throws IOException
    {
        ResourceLoader loader = new DefaultResourceLoader();
        InputStream is = loader.getResource(name).getInputStream();
        assertNotNull("Resource not found: " + name, is);

        return is;
    }

    protected User getAdmin() {
        return userManager.authenticate("admin", getPassword());
    }

    protected String getPassword() {
        return "admin";
    }

    protected void login(final String username, final String password) {
        AuthenticationProvider provider = (AuthenticationProvider) applicationContext.getBean("authenticationProvider");
        Authentication auth = provider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    protected boolean waitForIndexing(ArtifactVersion av) {
        int count = 0;
        while (count < 5000) {
            if (av.isIndexedPropertiesStale()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                count += 100;
            } else {
                try {
                    ((JcrVersion) av).getNode().refresh(true);
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    protected Artifact importHelloWsdl() 
        throws Exception {
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

    protected Artifact importXmlSchema() throws Exception {
        InputStream xsd = getResourceAsStream("/schema/test.xsd");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace, 
                                                    "application/xml", 
                                                    "test.xsd", 
                                                    "0.1", 
                                                    xsd, 
                                                    getAdmin());
        
        Artifact a = ar.getArtifact();
        return a;
    }

    protected Artifact importHelloMule() throws Exception {
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
//            JcrUtil.dump(node.getNode("groups"));
//            JcrUtil.dump(node.getNode("workspaces"));
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                Node child = itr.nextNode();
                if (!child.getName().startsWith("jcr:")) {
                    child.remove();
                }
            }
            session.save();
            session.logout();
        } catch (PathNotFoundException t) {
            // ignore
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml",
            "/META-INF/applicationContext-acegi-security.xml",
            "/META-INF/applicationContext-test.xml"
        };
    }

    @Override  
    protected void onSetUp() throws Exception {
        super.onSetUp();
        
        Session session;
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

        login("admin", getPassword());
    }

    @Override
    protected void onTearDown() throws Exception {
        logout();
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

    protected void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }


}
