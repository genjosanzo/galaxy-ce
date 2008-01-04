package org.mule.galaxy.impl;

import java.io.InputStream;
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
    public void testWsdlIndex() throws Exception {
        Set<Index> indices = registry.getIndices(Constants.WSDL_DEFINITION_QNAME);
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
    }
    
    public void testMuleIndex() throws Exception {
        Set<Index> indices = registry.getIndices(Constants.MULE_QNAME);
        assertNotNull(indices);
        assertEquals(1, indices.size());
        
        Index idx = indices.iterator().next();
        assertEquals("mule.service", idx.getId());
        assertEquals("Mule Services", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());
        
        // Import a document which should now be indexed
        InputStream helloWsdl = getResourceAsStream("/mule/hello-config.xml");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace, "application/xml", 
                                                    "hello-config.xml", 
                                                    "0.1", helloWsdl, getAdmin());
        Artifact artifact = ar.getArtifact();
        
        JcrVersion version = (JcrVersion) artifact.getActiveVersion();
        Object property = version.getProperty("mule.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;
        
        PropertyInfo pi = version.getPropertyInfo("mule.service");
        assertFalse(pi.isVisible());
        assertTrue(pi.isLocked());
        
        assertTrue(services.contains("GreeterUMO"));
        
        // Try out search!
        Set results = registry.search(new Query(Artifact.class, 
                                                Restriction.eq("mule.service", "GreeterUMO")));
        
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("mule.service", "GreeterUMO")));
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data
    }
}
