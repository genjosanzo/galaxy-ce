package org.mule.galaxy.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Settings;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.impl.cache.ThreadLocalCacheProviderFacade;
import org.mule.galaxy.impl.jcr.RegistryInitializer;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.plugin.PluginManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.query.FunctionRegistry;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.view.ArtifactViewManager;
import org.springframework.context.ConfigurableApplicationContext;
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
    protected FunctionRegistry functionRegistry;
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
    protected ArtifactViewManager artifactViewManager;
    protected TypeManager typeManager;
    protected EventManager eventManager;
    protected RegistryInitializer registryInitializer;
    
    private boolean participate;
    
    public AbstractGalaxyTest() {
        super();
        System.setProperty("galaxy.data", "./target/galaxy-data");
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
        ProviderManager provider = (ProviderManager) applicationContext.getBean("authenticationManager");
        Authentication auth = provider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    protected boolean waitForIndexing(Artifact av) {
        int count = 0;
        while (count < 5000) {
            if (!av.isIndexed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                count += 100;
            }
        }
        return false;
    }

    protected Item importHelloWsdl() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");

        return importFile(helloWsdl, "hello_world.wsdl", "0.1", "application/xml");
    }

    protected Item getTestWorkspace() throws RegistryException, AccessException {
        Collection<Item> workspaces = registry.getItems();
        assertEquals(1, workspaces.size());
        return workspaces.iterator().next();
    }

    protected Item importXmlSchema() throws Exception {
        InputStream xsd = getResourceAsStream("/schema/test.xsd");

        String name = "test.xsd";
        String version = "0.1";
        String contentType = "application/xml";
        
        return importFile(xsd, name, version, contentType);
    }

    protected Item importFile(InputStream stream, String name, String version, String contentType)
        throws Exception {
        
        Item workspace = getTestWorkspace();
        
        return importFile(workspace, stream, name, version, contentType);
    }

    protected Item importFile(Item workspace, InputStream stream, String name, String version,
                            String contentType) throws DuplicateItemException, RegistryException,
            PolicyException, PropertyException, AccessException, NotFoundException {
        NewItemResult result = workspace.newItem(name, typeManager.getTypeByName(TypeManager.ARTIFACT));
        Item artifact = (Item) result.getItem();

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("artifact", new Object[] { stream, contentType });
        NewItemResult ar = artifact.newItem(version, typeManager.getTypeByName(TypeManager.ARTIFACT_VERSION), props);

        return (Item) ar.getItem();
    }

    protected Item importHelloMule() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/mule2/hello-config.xml");
        
        return importFile(helloWsdl, "hello-config.xml", "0.1", "application/xml");
    }

    protected Type getSimpleType() throws NotFoundException {
        Type simpleType = typeManager.getTypeByName("Base Type");
        return simpleType;
    }
    
    protected Phase getPhase(Item item) {
        return (Phase) item.getProperty(Registry.PRIMARY_LIFECYCLE);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml",
            "/META-INF/applicationContext-acegi-security.xml",
            "/META-INF/applicationContext-cache.xml",            
            "classpath*:/META-INF/galaxy-applicationContext.xml",
            "/META-INF/applicationContext-test.xml"
        };
    }

    @Override
    protected ConfigurableApplicationContext loadContext(Object key) throws Exception {
        deleteIfExists(new File("target/galaxy-data/repository"));
        deleteIfExists(new File("target/galaxy-data/version"));
        deleteIfExists(new File("target/galaxy-data/workspaces"));
        return super.loadContext(key);
    }

    private void deleteIfExists(File file) throws IOException {
        if (file.exists()) {
            FileUtil.delete(file);
        }
    }

    @Override  
    protected void onSetUp() throws Exception {
        super.onSetUp();
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

        ThreadLocalCacheProviderFacade.enableCache();
        
        login("admin", getPassword());
    }

    @Override
    protected void onTearDown() throws Exception {
        logout();

        ThreadLocalCacheProviderFacade.clearCache();
        indexManager.destroy();

        if (repository != null) {
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
