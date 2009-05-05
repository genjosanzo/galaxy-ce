package org.mule.galaxy.impl;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.Links;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.link.LinkExtension;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;

public class LinkTest extends AbstractGalaxyTest {
    
    public void testLinkTypes() throws Exception {
        typeManager.getPropertyDescriptorByName(LinkExtension.DEPENDS, null);
    }
    
    public void testSingleLink() throws Exception {
        Item mule = importHelloMule();
        Item wsdl = importHelloWsdl();
        
        PropertyDescriptor pd = new PropertyDescriptor();
        pd.setProperty("single.link");
        pd.setDescription("Single Link");
        pd.setMultivalued(false);
        pd.setExtension((Extension)applicationContext.getBean("linkExtension"));
        Map<String, String> config = new HashMap<String,String>();
        config.put(LinkExtension.RECIPROCAL_CONFIG_KEY, "Single Link (Reciprocal)");
        pd.setConfiguration(config);
        typeManager.savePropertyDescriptor(pd);
        
        wsdl.setProperty(pd.getProperty(), mule);
        
        Item link = (Item) wsdl.getProperty(pd.getProperty());
        assertNotNull(link);
    }
    
    public void testWsdlDependencies() throws Exception {        
        Item schema = importFile(getResourceAsStream("/wsdl/imports/hello.xsd"), "hello.xsd", "0.1",
                "application/xml");
         
        Links links = (Links) schema.getProperty(LinkExtension.DEPENDS);
        Collection<Link> deps = links.getLinks();
        assertEquals(0, deps.size());
        
        Item portType = importFile(getResourceAsStream("/wsdl/imports/hello-portType.wsdl"), 
                                    "hello-portType.wsdl", 
                                    "0.1",
                                    "application/wsdl+xml");
        links = (Links) portType.getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        
        Link dep = deps.iterator().next();
        Item schemaEntry = schema.getParent();
        
        assertEquals("hello.xsd", dep.getLinkedToPath());
        assertNotNull(dep.getLinkedTo());
        assertEquals(schemaEntry.getId(), dep.getLinkedTo().getId());

        // figure out if the we can figure out which things link *to* the schema. 
        // We should find the portType
        links = (Links) schemaEntry.getProperty(LinkExtension.DEPENDS);
        Collection<Link> reciprocal = links.getReciprocalLinks();
        assertEquals(1, reciprocal.size());
        Link l = reciprocal.iterator().next();
        
        assertEquals(portType.getPath(), l.getItem().getPath());
        assertTrue(dep.isAutoDetected());
        
        Item svcWsdl = importFile(getResourceAsStream("/wsdl/imports/hello.wsdl"), 
                                                      "hello.wsdl", 
                                                      "0.1", 
                                                      "application/wsdl+xml");
        links = (Links) svcWsdl.getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        
        dep = deps.iterator().next();
        assertEquals(portType.getParent().getPath(), dep.getLinkedTo().getPath());
        assertTrue(dep.isAutoDetected());
        
        // Move the hello port type and see if that works
        registry.newItem("Test", typeManager.getType(TypeManager.WORKSPACE));
        registry.move(portType.getParent(), "/Test", "hello-portType.wsdl");
        
        // Ensure that the service wsdl still has a link, but it's linked to item should be null
        links = (Links) svcWsdl.getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        
        dep = deps.iterator().next();
        assertNull(dep.getLinkedTo());
        assertEquals("hello-portType.wsdl", dep.getLinkedToPath());
        assertTrue(dep.isAutoDetected());
        
        links = (Links) portType.getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        dep = deps.iterator().next();
        assertEquals("hello.xsd", dep.getLinkedToPath());
        assertNull(dep.getLinkedTo());
        
        // Move it back and see if it works
        registry.move(portType.getParent(), "/Default Workspace", "hello-portType.wsdl");
        
        Thread.sleep(1000);

//        links = (Links) portType.getProperty(LinkExtension.DEPENDS);
//        deps = links.getLinks();
//        assertEquals(1, deps.size());
//        dep = deps.iterator().next();
//        assertEquals("hello.xsd", dep.getLinkedToPath());
//        assertNotNull(dep.getLinkedTo());
//        System.out.println(dep.getLinkedTo().getPath());
//        assertEquals(schemaEntry.getId(), dep.getLinkedTo().getId());
//
//        // yeah, this doesn't make sense dependency-wise, but we're just seeing if user specified dependencies work
//        links = (Links) schema.getProperty(LinkExtension.DEPENDS);
//        links.addLinks(new Link(schema, svcWsdl.getParent(), null, false));
//        
//        deps = links.getLinks();
//        assertEquals(1, deps.size());
//        dep = deps.iterator().next();
//        assertEquals(svcWsdl.getParent().getId(), dep.getLinkedTo().getId());
//        assertFalse(dep.isAutoDetected());
//        
//        links.removeLinks(deps.iterator().next());
//        deps = links.getLinks();
//        assertEquals(0, deps.size());
    }
    
    public void testSchemaDependencies() throws Exception {
        Item schema = importFile(getResourceAsStream("/schema/hello.xsd"), 
                                                     "hello.xsd", 
                                                     "0.1",
                                                     "application/xml");
        
        Links links = (Links) schema.getProperty(LinkExtension.DEPENDS);
        Collection<Link> deps = links.getLinks();
        assertNotNull(deps);
        assertEquals(0, deps.size());

        Item schema2 = importFile(getResourceAsStream("/schema/hello-import.xsd"), 
                                                     "hello-import.xsd", 
                                                     "0.1",
                                                     "application/xml");
        
        // reload so the cache is fresh
        schema2 = registry.getItemById(schema2.getId());
        
        links = (Links) schema2.getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        Link dep = deps.iterator().next();
        assertEquals(schema.getParent().getId(), dep.getLinkedTo().getId());
        assertTrue(dep.isAutoDetected());
        

        Item schema3 = importFile(getResourceAsStream("/schema/hello-include.xsd"), 
                                                      "hello-include.xsd", 
                                                      "0.1",
                                                      "application/xml");

        // reload so the cache is fresh
        schema3 = registry.getItemById(schema3.getId());
        
        links = (Links) schema3.getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        dep = deps.iterator().next();
        assertEquals(schema.getParent().getId(), dep.getLinkedTo().getId());
        assertTrue(dep.isAutoDetected());
    }
    
    public void testAbsoluteSchemaDependencies() throws Exception {

        Item schema = importFile(getResourceAsStream("/schema/hello-import-absolute.xsd"), 
                                                     "hello-import-absolute.xsd", 
                                                     "0.1",
                                                     "application/xml");

        Links links = (Links) schema.getProperty(LinkExtension.DEPENDS);
        Collection<Link> deps = links.getLinks();
        assertNotNull(deps);
        assertEquals(2, deps.size());
        
        Link next = deps.iterator().next();
        assertNull(next.getLinkedTo());
    }
    
    public void testMissingWsdlDependencies() throws Exception {

        Item wsdl = importFile(getResourceAsStream("/wsdl/imports/hello-missing.wsdl"), 
                                                   "hello.wsdl", 
                                                   "0.1",
                                                   "application/xml");
        Links links = (Links) wsdl.getProperty(LinkExtension.DEPENDS);
        Collection<Link> deps = links.getLinks();
        assertEquals(1, deps.size());
        
        Link link = deps.iterator().next();
        assertNull(link.getLinkedTo());
        assertNotNull(link.getLinkedToPath());
        
    }
    
//    public void testConflicts() throws Exception {
//        Collection<Item> workspaces = registry.getItems();
//        assertEquals(1, workspaces.size());
//        Item workspace = workspaces.iterator().next();
//        
//        NewItemResult schema = workspace.createArtifact("application/xml", 
//                                                      "hello.xsd", 
//                                                      "0.1", 
//                                                      getResourceAsStream("/wsdl/imports/hello.xsd"));
//         
//        Links links = (Links) schema.getEntryVersion().getProperty(LinkExtension.CONFLICTS);
//        Collection<Link> deps = links.getLinks();
//        assertEquals(0, deps.size());
//        
//        deps = links.getReciprocalLinks();
//        assertEquals(0, deps.size());
//        
//        NewItemResult portType = workspace.createArtifact("application/wsdl+xml", 
//                                                        "hello-portType.wsdl", 
//                                                        "0.1", 
//                                                        getResourceAsStream("/wsdl/imports/hello-portType.wsdl"));
//        
//        EntryVersion version = portType.getEntryVersion();
//        
//        version.setProperty(LinkExtension.CONFLICTS, Arrays.asList(new Link(version, schema.getItem(), null, false)));
//        
//        Links ptLinks = (Links) version.getProperty(LinkExtension.CONFLICTS);
//        assertNotNull(ptLinks);
//        
//        deps = ptLinks.getLinks();
//        assertEquals(1, deps.size());
//        
//        deps = ptLinks.getReciprocalLinks();
//        assertEquals(0, deps.size());
//        
//        links = (Links) schema.getItem().getProperty(LinkExtension.CONFLICTS);
//        
//        deps = links.getLinks();
//        assertEquals(0, deps.size());
//        
//        deps = links.getReciprocalLinks();
//        assertEquals(1, deps.size());
//        
//        boolean conflicts = false;
//        boolean supercedes = false;
//        Collection<PropertyInfo> properties = schema.getItem().getProperties();
//        for (PropertyInfo pi : properties) {
//            if (pi.getName().equals(LinkExtension.CONFLICTS)) {
//                conflicts = true;
//            } else if (pi.getName().equals(LinkExtension.SUPERCEDES)) {
//                supercedes = true;
//            }
//        }
//        
//        assertTrue(conflicts);
//        assertFalse(supercedes);
//        
//        version.setProperty(LinkExtension.CONFLICTS, null);
//        
//        ptLinks = (Links) version.getProperty(LinkExtension.CONFLICTS);
//        assertNotNull(ptLinks);
//        
//        deps = ptLinks.getLinks();
//        assertEquals(0, deps.size());
//        
//        deps = ptLinks.getReciprocalLinks();
//        assertEquals(0, deps.size());
//    }
    
    public void testDelete() throws Exception{

        Item a1 = importFile(getResourceAsStream("/mule2/hello-config.xml"), 
                               "a1.xml", 
                               "0.1",
                               "application/xml");

        Item a2 = importFile(getResourceAsStream("/mule2/hello-config.xml"), 
                               "a2.xml", 
                               "0.1",
                               "application/xml");

        a2.setProperty(LinkExtension.CONFLICTS, Arrays.asList(new Link(a2, a1, null, false)));
        
        a1.delete();
        
        Links ptLinks = (Links) a2.getProperty(LinkExtension.CONFLICTS);
        assertNotNull(ptLinks);
        
        Collection<Link> deps = ptLinks.getLinks();
        assertEquals(0, deps.size());
        
        deps = ptLinks.getReciprocalLinks();
        assertEquals(0, deps.size());
    }
//    
//    public void testQuery() throws Exception{
//        Collection<Item> workspaces = registry.getItems();
//        assertEquals(1, workspaces.size());
//        Item workspace = workspaces.iterator().next();
//        
//        NewItemResult r1 = workspace.newEntry("a1", "0.1");
//        Entry a1 = r1.getItem();
//
//        NewItemResult r2 = workspace.newEntry("a2", "0.1");
//        EntryVersion v2 = r2.getEntryVersion();
//        
//        v2.setProperty(LinkExtension.CONFLICTS, Arrays.asList(new Link(v2, a1, null, false)));
//        
//        // test forward link
//        Query query = new Query(Entry.class).add(OpRestriction.eq(LinkExtension.CONFLICTS, "a1"));
//        
//        SearchResults results = registry.search(query);
//        assertEquals(1, results.getTotal());
//
//        // test full path
//        query = new Query().add(OpRestriction.eq(LinkExtension.CONFLICTS, r1.getItem().getPath()));
//        
//        results = registry.search(query);
//        assertEquals(1, results.getTotal());
//       
//        // test reciprocal
//        query = new Query().add(OpRestriction.eq(LinkExtension.CONFLICTS + ".reciprocal", v2.getPath()));
//        
//        results = registry.search(query);
//        assertEquals(1, results.getTotal());
//
//        // test reciprocal like
//        query = new Query().add(OpRestriction.like(LinkExtension.CONFLICTS + ".reciprocal", "a2?version=0.1"));
//        
//        results = registry.search(query);
//        assertEquals(1, results.getTotal());
//        
//        // test reciprocal like without version
//        query = new Query().add(OpRestriction.like(LinkExtension.CONFLICTS + ".reciprocal", "a2"));
//        
//        results = registry.search(query);
//        assertEquals(1, results.getTotal());
//        
//        // test IN
//        query = new Query(Entry.class).add(OpRestriction.in(LinkExtension.CONFLICTS, Arrays.asList("a2", "a1", "foo.xml")));
//        results = registry.search(query);
//        assertEquals(1, results.getTotal());
//
//        // test reciprocal IN
//        query = new Query().add(OpRestriction.in(LinkExtension.CONFLICTS + ".reciprocal", Arrays.asList("a2?version=0.1", "a1", "foo.xml")));
//        results = registry.search(query);
//        assertEquals(1, results.getTotal());
//    }
}
