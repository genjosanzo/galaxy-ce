package org.mule.galaxy.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Index;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.jcr.AbstractJcrObject;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
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
        assertEquals(1, indices.size());
        
        Index idx = indices.iterator().next();
        assertEquals("wsdl.service", idx.getId());
        assertEquals("WSDL Service", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(QName.class, idx.getQueryType());
        assertNotNull(idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());
        
        // Import a document which should now be indexed
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        Artifact artifact = registry.createArtifact(workspace, "application/xml", null, helloWsdl);
        
        AbstractJcrObject version = (AbstractJcrObject) artifact.getLatestVersion();
        Object property = version.getProperty("wsdl.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;
        
        assertTrue(services.contains("HelloWorldService"));
        
        // Try out search!
        Set results = registry.search(new Query(Artifact.class, 
                                                Restriction.eq("artifactVersion.wsdl.service", 
                                                               new QName("HelloWorldService"))));
        
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("artifactVersion.wsdl.service", 
                                                           new QName("HelloWorldService"))));
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        // assertNotNull(nextAV.getData());
        // TODO test data
    }

    public void testMuleIndex() throws Exception {
        Set<Index> indices = registry.getIndices(Constants.MULE_QNAME);
        assertNotNull(indices);
        assertEquals(1, indices.size());
        
        Index idx = indices.iterator().next();
        assertEquals("mule.service", idx.getId());
        assertEquals("Mule Service", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());
        
        // Import a document which should now be indexed
        InputStream helloWsdl = getResourceAsStream("/mule/hello-config.xml");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        Artifact artifact = registry.createArtifact(workspace, "application/xml", null, helloWsdl);
        
        AbstractJcrObject version = (AbstractJcrObject) artifact.getLatestVersion();
        Object property = version.getProperty("mule.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;
        
        assertTrue(services.contains("GreeterUMO"));
        
        // Try out search!
        Set results = registry.search(new Query(Artifact.class, 
                                                Restriction.eq("artifactVersion.mule.service", 
                                                               "GreeterUMO")));
        
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            Restriction.eq("artifactVersion.mule.service", 
                                                           "GreeterUMO")));
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        // assertNotNull(nextAV.getData());
        // TODO test data
    }
}
