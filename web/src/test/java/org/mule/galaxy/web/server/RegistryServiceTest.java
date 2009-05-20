package org.mule.galaxy.web.server;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.impl.artifact.UploadService;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.LinkInfo;
import org.mule.galaxy.web.rpc.RegistryService;
import org.mule.galaxy.web.rpc.SearchPredicate;
import org.mule.galaxy.web.rpc.WApprovalMessage;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WComment;
import org.mule.galaxy.web.rpc.WIndex;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WLinks;
import org.mule.galaxy.web.rpc.WPolicyException;
import org.mule.galaxy.web.rpc.WProperty;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.rpc.WSearchResults;
import org.mule.galaxy.web.rpc.WType;
import org.mule.galaxy.web.rpc.WUser;

public class RegistryServiceTest extends AbstractGalaxyTest {
    protected RegistryService gwtRegistry;
    protected UploadService uploadService;

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-core-extensions.xml", 
                              "/META-INF/applicationContext-acegi-security.xml", 
                              "/META-INF/applicationContext-web.xml",
                              "/META-INF/applicationContext-test.xml" };
    }

    protected Item importHelloTestWSDL() throws Exception
    {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello-noOperation.wsdl");

        return importFile(helloWsdl, "hello-noOperation.wsdl", "0.1", "application/xml");
    }

    public void testAddItem() throws Exception
    {
        HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("foo", "bar");
        
        Type type = getSimpleType();
        String id = gwtRegistry.addItem("/Default Workspace", "Test", null, type.getId(), props);
        
        ItemInfo info = gwtRegistry.getItemInfo(id, true);
        assertEquals("bar", info.getProperty("foo").getValue());
    }

    public void testAddArtifact() throws Exception
    {
        Type aType = typeManager.getTypeByName(TypeManager.ARTIFACT);
        Type avType = typeManager.getTypeByName(TypeManager.ARTIFACT_VERSION);
        
        String id = gwtRegistry.addItem("/Default Workspace", "test.wsdl", null, aType.getId(), null);
        
        String artifactId = uploadService.upload(getResourceAsStream("/wsdl/hello.wsdl"));
        
        HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("artifact", artifactId);
        
        id = gwtRegistry.addItem("/Default Workspace/test.wsdl", "1.0", null, avType.getId(), props);
        
        Item item = registry.getItemById(id);
        Artifact artifact = (Artifact) item.getProperty("artifact");
        assertNotNull(artifact);
    }
    
    
    public void testItemOperations() throws Exception
    {
        //importHelloTestWSDL();
        Collection<ItemInfo> workspaces = gwtRegistry.getItems(null);
        assertEquals(1, workspaces.size());

        Collection<WArtifactType> artifactTypes = gwtRegistry.getArtifactTypes();
        assertTrue(artifactTypes.size() > 0);
        
        // Grab a group of artifacts
        WSearchResults results = gwtRegistry.getArtifacts(null, null, true, new HashSet<SearchPredicate>(), null, 0, 20);
        
        List<String> columns = results.getColumns();
        assertTrue(columns.size() > 0);

        List<ItemInfo> rows = results.getRows();
        assertTrue(rows.size() > 0);
        
        Collection<ItemInfo> artifacts = results.getRows();
        ItemInfo wsdl = null;
        for (ItemInfo info : artifacts) {
            if (info.getName().equals("hello.wsdl"))
            {
                wsdl = info;
                break;
            }
        }
        assertNotNull(wsdl);

        // Test reretrieving the artifact
        ItemInfo entry = gwtRegistry.getItemInfo(wsdl.getId(), true);
        assertEquals("Artifact", entry.getType());
        
        gwtRegistry.setProperty(wsdl.getId(), "location", "Grand Rapids");
        
        Item artifact = registry.getItemById(wsdl.getId());
        assertEquals("Grand Rapids", artifact.getProperty("location"));
        artifact.setProperty("hidden", "value");
        artifact.setVisible("hidden", false);
        registry.save(artifact);
        
        // see if the hidden property shows up
        ItemInfo itemInfo = gwtRegistry.getItemInfo(artifact.getId(), true);
        
        WProperty hiddenProp = null;
        for (Object o : itemInfo.getProperties()) {
            WProperty prop = (WProperty) o;
            
            if (prop.getName().equals("hidden")) {
                hiddenProp = prop;
                break;
            }
        }
            
        assertNotNull(hiddenProp);
        
        // test links
        System.out.println(wsdl.getPath());
        Collection<ItemInfo> items = gwtRegistry.getItems(wsdl.getId());
        ItemInfo av = items.iterator().next();
        assertEquals(1, items.size());

        av = av.getItem("hello.wsdl").getItem("0.1");
        assertNotNull(av);
        
        av = gwtRegistry.getItemInfo(av.getId(), true);
        WProperty prop = av.getProperty("depends");
        assertNotNull(prop);
        
        WLinks links = (WLinks) prop.getValue();
        List<LinkInfo> deps = links.getLinks();
        assertEquals(1, deps.size());
        
        links = new WLinks();
        links.setLinks(new ArrayList<LinkInfo>());
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setItemName("/Default Workspace/hello.xsd");
        links.getLinks().add(linkInfo);
        
        gwtRegistry.setProperty(av.getId(), "conflicts", links);
        av = gwtRegistry.getItemInfo(av.getId(), true);
        prop = av.getProperty("conflicts");
        assertNotNull(prop);
        
        links = (WLinks) prop.getValue();
        deps = links.getLinks();
        assertEquals(1, deps.size());
        
        // try adding a comment
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        Object principal = auth.getPrincipal();
        assertNotNull(principal);

        WComment wc = gwtRegistry.addComment(wsdl.getId(), null, "Hello World");
        assertNotNull(wc);

        WComment wc2 = gwtRegistry.addComment(wsdl.getId(), wc.getId(), "Hello World");
        assertNotNull(wc2);

        // get the extended artifact info again
        ItemInfo ext = gwtRegistry.getItemInfo(wsdl.getId(), true);

        List<WComment> comments = ext.getComments();
        assertEquals(1, comments.size());

        WComment wc3 = comments.get(0);
        assertEquals(1, wc3.getComments().size());

        assertEquals("/api/registry/Default Workspace/hello.wsdl;history", ext.getArtifactFeedLink());
        assertEquals("/api/comments", ext.getCommentsFeedLink());
    }
    
    public void testWorkspaces() throws Exception {
        Collection<ItemInfo> workspaces = gwtRegistry.getItems(null);
        assertEquals(1, workspaces.size());
        
        ItemInfo w = workspaces.iterator().next();

        Type type = typeManager.getTypeByName(TypeManager.WORKSPACE);
        gwtRegistry.addItem(w.getPath(), "Foo", null, type.getId(), null);
        
        workspaces = gwtRegistry.getItems(w.getId());
        assertEquals(1, workspaces.size());
        
        w = workspaces.iterator().next();
        assertNotNull(w.getPath());
        
        // try adding an item to the top level
        gwtRegistry.addItem("", "Foo", null, type.getId(), null);
        gwtRegistry.addItem("/", "Foo2", null, type.getId(), null);
    }
    
    public void testEntries() throws Exception
    {
        //importHelloTestWSDL();
        Collection<ItemInfo> workspaces = gwtRegistry.getItems(null);
        assertEquals(1, workspaces.size());
        
        ItemInfo w = workspaces.iterator().next();

        Type type = getSimpleType();
        String entryId = gwtRegistry.addItem(w.getPath(), "Foo", null, type.getId(), new HashMap<String, Serializable>());
        
        ItemInfo entry = gwtRegistry.getItemInfo(entryId, false);
        assertEquals("Base Type", entry.getType());
        assertEquals("Foo", entry.getName());
        
        assertTrue(entry.isLocal());
    }
    
    public void testUserInfo() throws Exception {
        WUser user = gwtRegistry.getUserInfo();
        
        assertNotNull(user.getUsername());
        
        Collection<String> permissions = user.getPermissions();
        assertTrue(permissions.size() > 0);
        
        assertTrue(permissions.contains("MANAGE_USERS"));
    }
    
    public void testGovernanceOperations() throws Exception {
        Type type = getSimpleType();
        String id = gwtRegistry.addItem("/Default Workspace", "Test", null, type.getId(), null);
        
        ItemInfo info = gwtRegistry.getItemInfo(id, true);
     
        Item item = registry.getItemById(info.getId());
        Phase created = lifecycleManager.getLifecycle("Default").getPhase("Created");
        item.setProperty(Registry.PRIMARY_LIFECYCLE, created);
        registry.save(item);
        // get the item with the lifecycle info
        info = gwtRegistry.getItemInfo(id, true);
        WProperty property = info.getProperty(Registry.PRIMARY_LIFECYCLE);
        assertNotNull(property);
        assertNotNull(property.getListValue());
        
        List<String> ids = property.getListValue();
        assertEquals(created.getId(), ids.get(1));
        
        Phase developed = created.getNextPhases().iterator().next();
        gwtRegistry.setProperty(info.getId(), Registry.PRIMARY_LIFECYCLE, (Serializable) Arrays.asList(developed.getLifecycle().getId(), developed.getId()));
        
        // activate a policy which will make transitioning fail
        FauxPolicy policy = new FauxPolicy();
        policyManager.addPolicy(policy);

        // Try transitioning
        Phase tested = created.getNextPhases().iterator().next();
        try {
            gwtRegistry.setProperty(info.getId(), Registry.PRIMARY_LIFECYCLE, (Serializable) Arrays.asList(tested.getLifecycle().getId(), tested.getId()));
        } catch (WPolicyException e) {
            assertEquals(1, e.getPolicyFailures().size());
            
            List messages = (List) e.getPolicyFailures().values().iterator().next();
            
            WApprovalMessage msg = (WApprovalMessage) messages.iterator().next();
            assertEquals("Not approved", msg.getMessage());
            assertFalse(msg.isWarning());
        }
    }
    
    
    public void testIndexes() throws Exception {
        Collection<WIndex> indexes = gwtRegistry.getIndexes();
        
        assertTrue(indexes.size() > 0);
        
        WIndex idx = gwtRegistry.getIndex(indexes.iterator().next().getId());
        assertNotNull(idx.getId());
        assertNotNull(idx.getResultType());
        assertNotNull(idx.getIndexer());
        gwtRegistry.saveIndex(idx);
    }
    
    public void testLifecycles() throws Exception {
        Collection<WLifecycle> lifecycles = gwtRegistry.getLifecycles();
        
        assertEquals(1, lifecycles.size());
        
        WLifecycle wl = lifecycles.iterator().next();
        assertEquals("Default", wl.getName());
        assertNotNull(wl.getId());
     
        wl.setName("newname");
        
        gwtRegistry.saveLifecycle(wl);
        
        lifecycles = gwtRegistry.getLifecycles();
        assertEquals(1, lifecycles.size());
        
        wl = lifecycles.iterator().next();
        assertEquals("newname", wl.getName());
    }
    

    public void testTypes() throws Exception {
        WPropertyDescriptor wpd = new WPropertyDescriptor();
        wpd.setName("test");
        wpd.setDescription("test");
        gwtRegistry.savePropertyDescriptor(wpd);
        
        PropertyDescriptor pd = typeManager.getPropertyDescriptor(wpd.getId());
        assertNotNull(pd);
        
        WType type = new WType();
        type.setName("TestType");
        type.addProperty(wpd);
        
        gwtRegistry.saveType(type);
        
        WType type2 = gwtRegistry.getType(type.getId());
        assertEquals(type.getName(), type2.getName());
        assertEquals(1, type.getProperties().size());
    }
    
    /**
     * @throws Exception
     */
    public void testQueryToPredicate() throws Exception {
        Set<SearchPredicate> predicates = ((RegistryServiceImpl) gwtRegistry).getPredicates(Query.fromString("select where name != 'foo'"));
        
        assertEquals(1, predicates.size());
        
        SearchPredicate sp = predicates.iterator().next();
        
        assertEquals(SearchPredicate.DOES_NOT_HAVE_VALUE, sp.getMatchType());
        assertEquals("name", sp.getProperty());
        assertEquals("foo", sp.getValue());
    }
    
    public void testSuggestions() throws Exception {
        // Just ensuring we can handle some bad input
        gwtRegistry.suggestEntries("/", "xxx", new String[0]);
        gwtRegistry.suggestEntries("/Default", "xxx", new String[0]);
        gwtRegistry.suggestEntries("/D/", "xxx", new String[0]);
        gwtRegistry.suggestEntries("!@#$%^&*(){}[]?'\"><", "xxx", new String[0]);
    }
    
    private final class FauxPolicy implements Policy {
        public String getDescription() {
            return "Faux policy description";
        }

        public boolean applies(Item item) {
            return true;
        }

        public String getId() {
            return "faux";
        }

        public String getName() {
            return "Faux policy";
        }

        public Collection<ApprovalMessage> isApproved(Item item) {
            return Arrays.asList(new ApprovalMessage("Not approved"));
        }

        public void setRegistry(Registry registry) {
            
        }
    }

}
