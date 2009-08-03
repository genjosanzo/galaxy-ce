package org.mule.galaxy.plugins.mule2;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.impl.index.XQueryIndexer;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class IndexTest extends AbstractGalaxyTest {


    public void testIndexesMule22() throws Exception {
        testIndexes(getResourceAsStream("/mule2/hello-config.xml"), Constants.MULE2_2_QNAME);
    }
    
    public void testIndexesMule21() throws Exception {
        testIndexes(getResourceAsStream("/mule2/hello-config-21.xml"), Constants.MULE2_1_QNAME);
    }
    
    public void testIndexesMule20() throws Exception {
        testIndexes(getResourceAsStream("/mule2/hello-config-20.xml"), Constants.MULE2_0_QNAME);
    }
    
    public void testIndexes(InputStream is, QName muleQName) throws Exception {
        Collection<Index> indices = indexManager.getIndexes();
        assertNotNull(indices);
//        assertEquals(7, indices.size());
        Index idx = null;
        for (Iterator<Index> iterator = indices.iterator(); iterator.hasNext();)
        {
            idx = iterator.next();
            String prop = idx.getConfiguration().get("property");
            if("mule2.service".equals(prop))
            {
                break;
            }
        }
        assertNotNull(idx);
        assertEquals("Mule 2 Services", idx.getDescription());
        assertEquals("xquery", idx.getIndexer());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getConfiguration().get(XQueryIndexer.XQUERY_EXPRESSION));
        assertEquals("mule2.service", idx.getConfiguration().get(XQueryIndexer.PROPERTY_NAME));
        assertEquals(4, idx.getDocumentTypes().size());

        // Import a document which should now be indexed
        Item version = importFile(is, "hello-config.xml", "0.1", "application/xml");

        Artifact artifact = version.getProperty("artifact");
        assertEquals(muleQName, artifact.getDocumentType());
        
        Object property = version.getProperty("mule2.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;

        PropertyInfo pi = version.getPropertyInfo("mule2.service");
        assertTrue(pi.isVisible());
        assertTrue(pi.isLocked());

        assertTrue(services.contains("GreeterUMO"));

        // Try out search!
        Set<Item> results = registry.search(new Query(OpRestriction.eq("mule2.service", "GreeterUMO"))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query(OpRestriction.eq("mule2.service", "GreeterUMO"))).getResults();
        assertEquals(1, results.size());

        Item next = results.iterator().next();
        assertEquals("0.1", next.getName());
        // assertNotNull(nextAV.getData());
        // TODO test data
    }

}