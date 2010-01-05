package org.mule.galaxy.spring.config;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class IndexTest extends AbstractGalaxyTest
{


    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml",
            "/META-INF/applicationContext-core-extensions.xml",
            "/META-INF/applicationContext-acegi-security.xml",
            "/META-INF/applicationContext-cache.xml",            
            "classpath*:/META-INF/galaxy-applicationContext.xml",
            "/META-INF/applicationContext-test.xml"
        };
    }

    public void testSpringIndexes() throws Exception {
        Collection<Index> indices = indexManager.getIndexes();
        assertNotNull(indices);
//        assertEquals(2, indices.size());
        Index idx = null;
        for (final Index index : indices)
        {
            if (index.getDescription().contains("Spring Beans"))
            {
                idx = index;
                break;
            }
        }
        assertEquals("Spring Beans", idx.getDescription());
        assertEquals("xquery", idx.getIndexer());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getConfiguration().get("expression"));
        assertNotNull(idx.getConfiguration().get("property"));
        assertEquals(1, idx.getDocumentTypes().size());

        // Import a document which should now be indexed
        InputStream stream = getResourceAsStream("/spring/test-applicationContext.xml");

        Item version = importFile(stream, "test-applicationContext.xlm", "0.1", "application/xml");

        Object property = version.getProperty("spring.bean");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;

        PropertyInfo pi = version.getPropertyInfo("spring.bean");
        assertTrue(pi.isVisible());
        assertTrue(pi.isLocked());

        assertTrue(services.contains("TestObject1"));

        // Try out search!
        Set<Item> results = registry.search(new Query(OpRestriction.eq("spring.bean", "TestObject1"))).getResults();

        assertEquals(1, results.size());

        Item next = results.iterator().next();
        assertEquals("0.1", next.getName());
    }

}