package org.mule.galaxy.impl;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactResult;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Index;
import org.mule.galaxy.api.PropertyInfo;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.util.Constants;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.query.QueryImpl;
import org.mule.galaxy.query.RestrictionImpl;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

public class IndexTest extends AbstractGalaxyTest {

    
    public void xtestReindex() throws Exception {
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
        assertEquals(String.class, idx.getQueryType());
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
        
        assertTrue(services.contains("HelloWorldService"));
        
        property = version.getProperty("wsdl.targetNamespace");
        assertNotNull(property);
        assertEquals("http://mule.org/hello_world", property);
        
        property = version.getProperty(epIdx.getId());
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection endpoints = (Collection) property;
        assertTrue(endpoints.contains("SoapPort"));
        
        // Try out search!
        Set results = registry.search("select artifact where wsdl.service = 'HelloWorldService'", 0, 100).getResults();
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new QueryImpl(Artifact.class,
                                                RestrictionImpl.eq("wsdl.service",
                                                               new QName("HelloWorldService")))).getResults();
        
        assertEquals(1, results.size());
        
        next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new QueryImpl(ArtifactVersion.class,
                                            RestrictionImpl.eq("wsdl.service",
                                                           new QName("HelloWorldService")))).getResults();
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data
        
        results = registry.search(new QueryImpl(ArtifactVersion.class,
                                            RestrictionImpl.eq("documentType", Constants.WSDL_DEFINITION_QNAME.toString()))).getResults();
    
        assertEquals(1, results.size());
        
        results = registry.search(new QueryImpl(ArtifactVersion.class,
                                            RestrictionImpl.eq("contentType", "application/xml"))).getResults();
    
        assertEquals(1, results.size());
        

        results = registry.search(new QueryImpl(ArtifactVersion.class,
                                            RestrictionImpl.in("contentType", Arrays.asList("application/xml")))).getResults();
    
        assertEquals(1, results.size());
    }
}
