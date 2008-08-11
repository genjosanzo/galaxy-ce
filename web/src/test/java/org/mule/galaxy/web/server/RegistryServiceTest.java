package org.mule.galaxy.web.server;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.link.LinkExtension;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.web.rpc.EntryGroup;
import org.mule.galaxy.web.rpc.EntryInfo;
import org.mule.galaxy.web.rpc.EntryVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedEntryInfo;
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
import org.mule.galaxy.web.rpc.WUser;
import org.mule.galaxy.web.rpc.WWorkspace;

public class RegistryServiceTest extends AbstractGalaxyTest {
    protected RegistryService gwtRegistry;

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-acegi-security.xml", 
                              "/META-INF/applicationContext-web.xml",
                              "/META-INF/applicationContext-test.xml" };
    }

    protected Artifact importHelloTestWSDL() throws Exception
    {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello-noOperation.wsdl");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        EntryResult ar = workspace.createArtifact("application/xml",
                                                     "hello-noOperation.wsdl",
                                                     "0.1", 
                                                     helloWsdl, 
                                                     getAdmin());
        return (Artifact) ar.getEntry();
    }

    public void testArtifactOperations() throws Exception
    {
        //importHelloTestWSDL();
        Collection<WWorkspace> workspaces = gwtRegistry.getWorkspaces();
        assertEquals(1, workspaces.size());

        Collection<WArtifactType> artifactTypes = gwtRegistry.getArtifactTypes();
        assertTrue(artifactTypes.size() > 0);
        
        // Grab a group of artifacts
        Collection<EntryGroup> artifacts = gwtRegistry.getArtifacts(null, null, true, null, new HashSet<SearchPredicate>(), null, 0, 20).getResults();

        assertTrue(artifacts.size() > 0);

        EntryGroup g1 = null;
        EntryInfo info = null;
        for (Iterator<EntryGroup> itr = artifacts.iterator(); itr.hasNext();) {
            EntryGroup group = itr.next();

            if ("WSDL Documents".equals(group.getName())) {
                for (Iterator<EntryInfo> itr2 = group.getRows().iterator(); itr2.hasNext();)
                {
                    info = itr2.next();
                    if(info.getName().equals("hello.wsdl"))
                    {
                        g1 = group;
                        break;
                    }
                }
            }
        }
        assertNotNull(g1);

        List<String> columns = g1.getColumns();
        assertTrue(columns.size() > 0);

        List<EntryInfo> rows = g1.getRows();
        assertEquals(2, rows.size());

        WLinks links = gwtRegistry.getLinks(info.getId(), LinkExtension.DEPENDS);
        List<LinkInfo> deps = links.getLinks();
        assertEquals(1, deps.size());

        // Test reretrieving the artifact
        ExtendedEntryInfo entry = gwtRegistry.getEntry(info.getId());
        assertEquals("WSDL Documents", entry.getType());
        
        gwtRegistry.setProperty(info.getId(), "location", "Grand Rapids");
        
        Artifact artifact = (Artifact) registry.getItemById(info.getId());
        assertEquals("Grand Rapids", artifact.getProperty("location"));
        artifact.setProperty("hidden", "value");
        artifact.setVisible("hidden", false);
        registry.save(artifact);
        
        // see if the hidden property shows up
        EntryVersionInfo av = gwtRegistry.getEntryVersionInfo(artifact.getDefaultOrLastVersion().getId(), true);
        
        WProperty hiddenProp = null;
        for (Object o : av.getProperties()) {
            WProperty prop = (WProperty) o;
            
            if (prop.getName().equals("hidden")) {
                hiddenProp = prop;
                break;
            }
        }
            
        assertNotNull(hiddenProp);
        
        // try adding a comment
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        Object principal = auth.getPrincipal();
        assertNotNull(principal);

        WComment wc = gwtRegistry.addComment(info.getId(), null, "Hello World");
        assertNotNull(wc);

        WComment wc2 = gwtRegistry.addComment(info.getId(), wc.getId(), "Hello World");
        assertNotNull(wc2);

        // get the extended artifact info again
        ExtendedEntryInfo ext = gwtRegistry.getEntry(info.getId());

        List<WComment> comments = ext.getComments();
        assertEquals(1, comments.size());

        WComment wc3 = comments.get(0);
        assertEquals(1, wc3.getComments().size());

        assertEquals("/api/registry/Default Workspace/hello.wsdl", ext.getArtifactLink());
        assertEquals("/api/comments", ext.getCommentsFeedLink());

        // test desc
        gwtRegistry.setDescription(info.getId(), "test desc");
    }
    
    public void testWorkspaces() throws Exception {
        Collection<WWorkspace> workspaces = gwtRegistry.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        WWorkspace w = workspaces.iterator().next();
        
        gwtRegistry.addWorkspace(w.getId(), "Foo", null);
        
        workspaces = gwtRegistry.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        w = workspaces.iterator().next();
        assertNotNull(w.getWorkspaces());
        assertEquals(1, w.getWorkspaces().size());
        
        assertNotNull(w.getPath());
    }
    public void testEntries() throws Exception
    {
        //importHelloTestWSDL();
        Collection<WWorkspace> workspaces = gwtRegistry.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        WWorkspace w = workspaces.iterator().next();
        
        String entryId = gwtRegistry.newEntry(w.getId(), "Foo", "1");
        
        ExtendedEntryInfo entry = gwtRegistry.getEntry(entryId);
        assertEquals("Entry", entry.getType());
        assertEquals("Foo", entry.getName());
        
        assertFalse(entry.isArtifact());
        
        String vId = gwtRegistry.newEntryVersion(entryId, "2");
        
        EntryVersionInfo info = gwtRegistry.getEntryVersionInfo(vId, false);
        assertNotNull(info);
        
    }
    
    public void testUserInfo() throws Exception {
        WUser user = gwtRegistry.getUserInfo();
        
        assertNotNull(user.getUsername());
        
        Collection<String> permissions = user.getPermissions();
        assertTrue(permissions.size() > 0);
        
        assertTrue(permissions.contains("MANAGE_USERS"));
    }
    
    public void testGovernanceOperations() throws Exception {
        Collection<EntryGroup> artifacts = gwtRegistry.getArtifacts(null, null, true, null, new HashSet<SearchPredicate>(), null, 0, 20).getResults();
        EntryGroup g1 = artifacts.iterator().next();
        
        EntryInfo a = g1.getRows().get(0);
        ExtendedEntryInfo ext = gwtRegistry.getEntry(a.getId());
        
        Collection<EntryVersionInfo> versions = ext.getVersions();
        EntryVersionInfo v = versions.iterator().next();
     
        Phase created = lifecycleManager.getLifecycle("Default").getPhase("Created");
        
        WProperty property = v.getProperty(Registry.PRIMARY_LIFECYCLE);
        assertNotNull(property);
        assertNotNull(property.getListValue());
        
        List<String> ids = property.getListValue();
        assertEquals(created.getId(), ids.get(1));
        
        Phase developed = created.getNextPhases().iterator().next();
        gwtRegistry.setProperty(a.getId(), Registry.PRIMARY_LIFECYCLE, Arrays.asList(developed.getLifecycle().getId(), developed.getId()));
        
        // activate a policy which will make transitioning fail
        FauxPolicy policy = new FauxPolicy();
        policyManager.addPolicy(policy);

        // Try transitioning
        Phase tested = created.getNextPhases().iterator().next();
        try {
            gwtRegistry.setProperty(a.getId(), Registry.PRIMARY_LIFECYCLE, Arrays.asList(tested.getLifecycle().getId(), tested.getId()));
        } catch (WPolicyException e) {
            assertEquals(1, e.getPolicyFailures().size());
            
            List messages = (List) e.getPolicyFailures().values().iterator().next();
            
            WApprovalMessage msg = (WApprovalMessage) messages.iterator().next();
            assertEquals("Not approved", msg.getMessage());
            assertFalse(msg.isWarning());
        }
    }
    
    public void testVersioningOperations() throws Exception {
        Set result = registry.search("select artifact where wsdl.service = 'HelloWorldService'", 0, 100).getResults();
        
        Artifact a = (Artifact) result.iterator().next();
        
        a.newVersion(getResourceAsStream("/wsdl/imports/hello.wsdl"), "0.2", getAdmin());
        
        ExtendedEntryInfo ext = gwtRegistry.getEntry(a.getId());
        
        Collection<EntryVersionInfo> versions = ext.getVersions();
        assertEquals(2, versions.size());
        
        EntryVersionInfo info = versions.iterator().next();
        assertEquals("0.2", info.getVersionLabel());
        assertNotNull(info.getLink());
        assertNotNull(info.getCreated());
        assertEquals("Administrator", info.getAuthorName());
        assertEquals("admin", info.getAuthorUsername());
        
        gwtRegistry.setDefault(info.getId());
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
    
    public void testPDs() throws Exception {
        WPropertyDescriptor wpd = new WPropertyDescriptor();
        
        wpd.setName("test");
        wpd.setDescription("test");
        
        gwtRegistry.savePropertyDescriptor(wpd);
        
        PropertyDescriptor pd = typeManager.getPropertyDescriptor(wpd.getId());
        
        assertNotNull(pd);
    }
    
    /**
     * @throws Exception
     */
    public void testQueryToPredicate() throws Exception {
        Set<SearchPredicate> predicates = ((RegistryServiceImpl) gwtRegistry).getPredicates(Query.fromString("select artifact where name != 'foo'"));
        
        assertEquals(1, predicates.size());
        
        SearchPredicate sp = predicates.iterator().next();
        
        assertEquals(SearchPredicate.DOES_NOT_HAVE_VALUE, sp.getMatchType());
        assertEquals("name", sp.getProperty());
        assertEquals("foo", sp.getValue());
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
