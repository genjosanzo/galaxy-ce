package org.mule.galaxy.impl.query;

import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.query.AbstractFunction;
import org.mule.galaxy.query.FunctionCall;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class FunctionQueryTest extends AbstractGalaxyTest {
    public void testRemoveOddChars() throws Exception {
        assertTrue(functionRegistry != null);
        
        List<AbstractFunction> functions = functionRegistry.getFunctions();
        assertEquals(1, functions.size());
        
        AbstractFunction fn = functionRegistry.getFunction("test", "removeOddChars");
        
        assertNotNull(fn);
        
        Query query = new Query(Artifact.class);
        
        query.add(new FunctionCall("test", "removeOddChars"));
        
        Artifact a = importHelloMule();
        a.setName("odd1.wsdl");
        registry.save(a);
        
        // this one is even
        importHelloWsdl();
        
        SearchResults search = registry.search(query);
        
        assertEquals(1, search.getTotal());
    }
}
