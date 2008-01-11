package org.mule.galaxy.impl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Index;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class IndexTest extends AbstractGalaxyTest {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
        
    }
    
    public void testReindex() throws Exception {
        Artifact artifact = importHelloWsdl();
        
        Index i = indexManager.getIndex("wsdl.targetNamespace");
        
        i.setExpression("concat('foo', 'bar')");
        
        indexManager.save(i);
        
        Thread.sleep(2000);
        
        artifact = registry.getArtifact(artifact.getId());
        Object value = artifact.getProperty("wsdl.targetNamespace");
        assertEquals("foobar", value);
    }
    
    public void testWsdlIndex() throws Exception {
        Collection<Index> indices = indexManager.getIndices(Constants.WSDL_DEFINITION_QNAME);
        assertNotNull(indices);
        assertEquals(5, indices.size());
        
        Index idx = null;
        Index tnsIdx = null;
        Index epIdx = null;
        for (Index i : indices) {
            if (i.getId().equals("wsdl.service")) {
                idx = i;
            } else if (i.getId().equals("wsdl.endpoint")) {
                epIdx = i;
            } else if (i.getId().equals("wsdl.targetNamespace")) {
                tnsIdx = i;
            }
        }
        
        assertEquals("wsdl.service", idx.getId());
        assertEquals("WSDL Services", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(QName.class, idx.getQueryType());
        assertNotNull(idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());
        
        assertEquals("wsdl.targetNamespace", tnsIdx.getId());
        assertEquals(Index.Language.XPATH, tnsIdx.getLanguage());
        assertEquals(String.class, tnsIdx.getQueryType());
        assertNotNull(tnsIdx.getExpression());
        assertEquals(1, tnsIdx.getDocumentTypes().size());
        
        // Import a document which should now be indexed
        Artifact artifact = importHelloWsdl();

        ArtifactVersion version = artifact.getActiveVersion();
        Object property = version.getProperty("wsdl.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;
        
        assertTrue(services.contains(new QName("HelloWorldService")));
        
        property = version.getProperty("wsdl.targetNamespace");
        assertNotNull(property);
        assertEquals("http://mule.org/hello_world", property);
        
        property = version.getProperty(epIdx.getId());
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection endpoints = (Collection) property;
        assertTrue(endpoints.contains(new QName("SoapPort")));
        
        // Try out search!
        Set results = registry.search("select artifact where wsdl.service = 'HelloWorldService'");
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(Artifact.class, 
                                                Restriction.eq("wsdl.service", 
                                                               new QName("HelloWorldService"))));
        
        assertEquals(1, results.size());
        
        next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("wsdl.service", 
                                                           new QName("HelloWorldService"))));
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("documentType", Constants.WSDL_DEFINITION_QNAME.toString())));
    
        assertEquals(1, results.size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("contentType", "application/xml")));
    
        assertEquals(1, results.size());
        

        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.in("contentType", Arrays.asList("application/xml"))));
    
        assertEquals(1, results.size());
    }
    
    public void testMule2Index() throws Exception {
        Collection<Index> indices = indexManager.getIndices(Constants.MULE2_QNAME);
        assertNotNull(indices);
        assertEquals(1, indices.size());
        
        Index idx = indices.iterator().next();
        assertEquals("mule2.service", idx.getId());
        assertEquals("Mule 2 Services", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());
        
        // Import a document which should now be indexed
        InputStream helloWsdl = getResourceAsStream("/mule2/hello-config.xml");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace, "application/xml", 
                                                    "hello-config.xml", 
                                                    "0.1", helloWsdl, getAdmin());
        Artifact artifact = ar.getArtifact();
        
        JcrVersion version = (JcrVersion) artifact.getActiveVersion();
        Object property = version.getProperty("mule2.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;
        
        PropertyInfo pi = version.getPropertyInfo("mule2.service");
        assertTrue(pi.isVisible());
        assertTrue(pi.isLocked());
        
        assertTrue(services.contains("GreeterUMO"));
        
        // Try out search!
        Set results = registry.search(new Query(Artifact.class, 
                                                Restriction.eq("mule2.service", "GreeterUMO")));
        
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("mule2.service", "GreeterUMO")));
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data
    }
    
    public void testMuleIndex() throws Exception {
        
        // Import a document which should now be indexed
        InputStream helloConfig = getResourceAsStream("/mule/hello-config.xml");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace, 
                                                    "application/xml", 
                                                    "hello-config.xml", 
                                                    "0.1", helloConfig, getAdmin());
        Artifact artifact = ar.getArtifact();
        
        JcrVersion version = (JcrVersion) artifact.getActiveVersion();
        Object property = version.getProperty("mule.description");
        assertNotNull(property);
        assertTrue(property instanceof String);
    }
}
