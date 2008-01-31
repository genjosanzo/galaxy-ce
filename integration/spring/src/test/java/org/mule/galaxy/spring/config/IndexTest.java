package org.mule.galaxy.spring.config;

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


    public void testSpringIndexes() throws Exception {
        Collection<Index> indices = indexManager.getIndices(Constants.SPRING_QNAME);
        assertNotNull(indices);
        assertEquals(2, indices.size());
        Index idx = null;
        for (Iterator<Index> iterator = indices.iterator(); iterator.hasNext();)
        {
            idx = iterator.next();
            if("spring.bean".equals(idx.getId()))
            {
                break;
            }
        }
        assertNotNull(idx);
        assertEquals("spring.bean", idx.getId());
        assertEquals("Spring Beans", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());

        // Import a document which should now be indexed
        InputStream stream = getResourceAsStream("/spring/test-applicationContext.xml");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactResult ar = registry.createArtifact(workspace, "application/xml",
                                                    "test-applicationContext.xml",
                                                    "0.1", stream, getAdmin());
        Artifact artifact = ar.getArtifact();

        JcrVersion version = (JcrVersion) artifact.getActiveVersion();
        Object property = version.getProperty("spring.bean");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;

        PropertyInfo pi = version.getPropertyInfo("spring.bean");
        assertTrue(pi.isVisible());
        assertTrue(pi.isLocked());

        assertTrue(services.contains("TestObject1"));

        // Try out search!
        Set results = registry.search(new QueryImpl(Artifact.class,
                                                RestrictionImpl.eq("spring.bean", "TestObject1"))).getResults();

        assertEquals(1, results.size());

        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());

        results = registry.search(new QueryImpl(ArtifactVersion.class,
                                            RestrictionImpl.eq("spring.bean", "TestObject1"))).getResults();

        assertEquals(1, results.size());

        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data

        //TODO Query Tests
        //"select artifact where spring.bean = 'TestObject1'" 1
        //"select artifact where spring.bean = 'TestObject2'" 1
        //"select artifact where spring.bean = 'TestObjectXX'" 0
        //"select artifact where spring.description = 'Test Sprng Application Context'" 1
    }
   
}