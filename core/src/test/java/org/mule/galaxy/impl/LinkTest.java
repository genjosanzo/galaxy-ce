package org.mule.galaxy.impl;


import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Link;
import org.mule.galaxy.Links;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.link.LinkExtension;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class LinkTest extends AbstractGalaxyTest {
    
    public void testLinkTypes() throws Exception {
        typeManager.getPropertyDescriptorByName(LinkExtension.DEPENDS);
    }
    
    public void testWsdlDependencies() throws Exception {
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        EntryResult schema = workspace.createArtifact("application/xml", 
                                                         "hello.xsd", 
                                                         "0.1", 
                                                         getResourceAsStream("/wsdl/imports/hello.xsd"), 
                                                         getAdmin());
         
        Links links = (Links) schema.getEntryVersion().getProperty(LinkExtension.DEPENDS);
        Collection<Link> deps = links.getLinks();
        assertEquals(0, deps.size());
        
        EntryResult portType = workspace.createArtifact("application/wsdl+xml", 
                                                        "hello-portType.wsdl", 
                                                        "0.1", 
                                                        getResourceAsStream("/wsdl/imports/hello-portType.wsdl"), 
                                                        getAdmin());
        links = (Links) portType.getEntryVersion().getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        
        Link dep = deps.iterator().next();
        Entry schemaEntry = schema.getEntry();
        
        assertEquals("hello.xsd", dep.getLinkedToPath());
        assertNotNull(dep.getLinkedTo());
        assertEquals(schemaEntry.getId(), dep.getLinkedTo().getId());

        // figure out if the we can figure out which things link *to* the schema. 
        // We should find the portType
        links = (Links) schemaEntry.getProperty(LinkExtension.DEPENDS);
        Collection<Link> reciprocal = links.getReciprocalLinks();
        assertEquals(1, reciprocal.size());
        Link l = reciprocal.iterator().next();
        Artifact parent = (Artifact) l.getItem().getParent();
        assertEquals(portType.getEntry().getId(), parent.getId());
        assertTrue(dep.isAutoDetected());
        
        EntryResult svcWsdl = workspace.createArtifact("application/wsdl+xml", 
                                                          "hello.wsdl", 
                                                          "0.1", 
                                                          getResourceAsStream("/wsdl/imports/hello.wsdl"), 
                                                          getAdmin());
        links = (Links) svcWsdl.getEntryVersion().getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        
        dep = deps.iterator().next();
        assertEquals(portType.getEntry().getId(), dep.getLinkedTo().getId());
        assertTrue(dep.isAutoDetected());
        
        // yeah, this doesn't make sense dependency-wise, but we're just seeing if user specified dependencies work
        EntryVersion schemaV = schema.getEntryVersion();
        links = (Links) schemaV.getProperty(LinkExtension.DEPENDS);
        
        links.addLinks(new Link(schemaV, svcWsdl.getEntry(), null, false));
        
        deps = links.getLinks();
        assertEquals(1, deps.size());
        dep = deps.iterator().next();
        assertEquals(svcWsdl.getEntry().getId(), dep.getLinkedTo().getId());
        assertFalse(dep.isAutoDetected());
        
        links.removeLinks(deps.iterator().next());
        deps = links.getLinks();
        assertEquals(0, deps.size());
    }
    
    public void testSchemaDependencies() throws Exception {

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        EntryResult schema = workspace.createArtifact(
                                                        "application/xml", 
                                                        "hello.xsd", 
                                                        "0.1", 
                                                        getResourceAsStream("/schema/hello.xsd"), 
                                                        getAdmin());
        
        Links links = (Links) schema.getEntryVersion().getProperty(LinkExtension.DEPENDS);
        Collection<Link> deps = links.getLinks();
        assertNotNull(deps);
        assertEquals(0, deps.size());
        
        EntryResult schema2 = workspace.createArtifact("application/xml", 
                                                          "hello-import.xsd", 
                                                          "0.1", 
                                                          getResourceAsStream("/schema/hello-import.xsd"), 
                                                          getAdmin());
        
        EntryVersion s2version = schema2.getEntryVersion();
        
        // reload so the cache is fresh
        s2version = (EntryVersion) registry.getItemById(s2version.getId());
        
        links = (Links) s2version.getProperty(LinkExtension.DEPENDS);
        deps = links.getLinks();
        assertEquals(1, deps.size());
        Link dep = deps.iterator().next();
        assertEquals(schema.getEntry().getId(), dep.getLinkedTo().getId());
        assertTrue(dep.isAutoDetected());
    }
    
    public void testMissingWsdlDependencies() throws Exception {

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        EntryResult svcWsdl = workspace.createArtifact("application/wsdl+xml", 
                                                       "hello.wsdl", 
                                                       "0.1", 
                                                       getResourceAsStream("/wsdl/imports/hello-missing.wsdl"), 
                                                       getAdmin());
        Links links = (Links) svcWsdl.getEntryVersion().getProperty(LinkExtension.DEPENDS);
        Collection<Link> deps = links.getLinks();
        assertEquals(1, deps.size());
        
        Link link = deps.iterator().next();
        assertNull(link.getLinkedTo());
        assertNotNull(link.getLinkedToPath());
        
    }
    
    public void testConflicts() throws Exception {
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        EntryResult schema = workspace.createArtifact("application/xml", 
                                                         "hello.xsd", 
                                                         "0.1", 
                                                         getResourceAsStream("/wsdl/imports/hello.xsd"), 
                                                         getAdmin());
         
        Links links = (Links) schema.getEntryVersion().getProperty(LinkExtension.CONFLICTS);
        Collection<Link> deps = links.getLinks();
        assertEquals(0, deps.size());
        
        deps = links.getReciprocalLinks();
        assertEquals(0, deps.size());
        
        EntryResult portType = workspace.createArtifact("application/wsdl+xml", 
                                                        "hello-portType.wsdl", 
                                                        "0.1", 
                                                        getResourceAsStream("/wsdl/imports/hello-portType.wsdl"), 
                                                        getAdmin());
        
        EntryVersion version = portType.getEntryVersion();
        
        version.setProperty(LinkExtension.CONFLICTS, Arrays.asList(new Link(version, schema.getEntry(), null, false)));
        
        Links ptLinks = (Links) version.getProperty(LinkExtension.CONFLICTS);
        assertNotNull(ptLinks);
        
        deps = ptLinks.getLinks();
        assertEquals(1, deps.size());
        
        deps = ptLinks.getReciprocalLinks();
        assertEquals(0, deps.size());
        
        links = (Links) schema.getEntryVersion().getProperty(LinkExtension.CONFLICTS);
        
        deps = links.getLinks();
        assertEquals(0, deps.size());
        
        deps = links.getReciprocalLinks();
        assertEquals(1, deps.size());
        
        boolean conflicts = false;
        boolean supercedes = false;
        Collection<PropertyInfo> properties = schema.getEntry().getProperties();
        for (PropertyInfo pi : properties) {
            if (pi.getName().equals(LinkExtension.CONFLICTS)) {
                conflicts = true;
            } else if (pi.getName().equals(LinkExtension.SUPERCEDES)) {
                supercedes = true;
            }
        }
        
        assertTrue(conflicts);
        assertFalse(supercedes);
        
        version.setProperty(LinkExtension.CONFLICTS, null);
        
        ptLinks = (Links) version.getProperty(LinkExtension.CONFLICTS);
        assertNotNull(ptLinks);
        
        deps = ptLinks.getLinks();
        assertEquals(0, deps.size());
        
        deps = ptLinks.getReciprocalLinks();
        assertEquals(0, deps.size());
    }
    
    public void testDelete() throws Exception{
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        EntryResult r1 = workspace.createArtifact("application/xml", 
                                                  "a1.xml", 
                                                  "0.1", 
                                                  getResourceAsStream("/mule/hello-config.xml"), 
                                                  getAdmin());
        Entry a1 = r1.getEntry();

        EntryResult r2 = workspace.createArtifact("application/xml", 
                                                  "a2.xml", 
                                                  "0.1", 
                                                  getResourceAsStream("/mule/hello-config.xml"), 
                                                  getAdmin());
        EntryVersion v2 = r2.getEntryVersion();
        
        v2.setProperty(LinkExtension.CONFLICTS, Arrays.asList(new Link(v2, a1, null, false)));
        
        a1.delete();
        
        Links ptLinks = (Links) v2.getProperty(LinkExtension.CONFLICTS);
        assertNotNull(ptLinks);
        
        Collection<Link> deps = ptLinks.getLinks();
        assertEquals(0, deps.size());
        
        deps = ptLinks.getReciprocalLinks();
        assertEquals(0, deps.size());
    }

}
