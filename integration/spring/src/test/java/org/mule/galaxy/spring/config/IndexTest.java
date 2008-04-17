package org.mule.galaxy.spring.config;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

public class IndexTest extends AbstractGalaxyTest
{


    public void testSpringIndexes() throws Exception {
        Collection<Index> indices = indexManager.getIndexes();
        assertNotNull(indices);
//        assertEquals(2, indices.size());
        Index idx = null;
        for (final Index index : indices)
        {
            idx = index;
            if ("spring.bean".equals(idx.getId()))
            {
                break;
            }
        }
        assertNotNull(idx);
        assertEquals("spring.bean", idx.getId());
        assertEquals("Spring Beans", idx.getDescription());
        assertEquals("xquery", idx.getIndexer());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getConfiguration().get("expression"));
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

        JcrVersion version = (JcrVersion) artifact.getDefaultVersion();
        Object property = version.getProperty("spring.bean");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;

        PropertyInfo pi = version.getPropertyInfo("spring.bean");
        assertTrue(pi.isVisible());
        assertTrue(pi.isLocked());

        assertTrue(services.contains("TestObject1"));

        // Try out search!
        Set results = registry.search(new Query(Artifact.class,
                                                Restriction.eq("spring.bean", "TestObject1"))).getResults();

        assertEquals(1, results.size());

        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());

        results = registry.search(new Query(ArtifactVersion.class,
                                            Restriction.eq("spring.bean", "TestObject1"))).getResults();

        assertEquals(1, results.size());

        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data

        //TODO Query Tests
        //"select artifact where spring.bean = 'TestObject1'" 1
        //"select artifact where spring.bean = 'TestObject2'" 1
        //"select artifact where spring.bean = 'TestObjectXX'" 0
        //"select artifact where spring.description = 'Test Spring Application Context'" 1
    }

}