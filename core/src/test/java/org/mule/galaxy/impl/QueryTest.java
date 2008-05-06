package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class QueryTest extends AbstractGalaxyTest {
    
    public void testQueries() throws Exception {
        importXmlSchema();

        // Try out search!
        Query q = new Query(Artifact.class)
            .add(OpRestriction.in("phase", 
                                Arrays.asList(new String[] { "Default:Created", "Default:Developed" })));
        Set results = registry.search(q).getResults();

        assertEquals(1, results.size());

        q.setStart(1);
        results = registry.search(q).getResults();
        assertEquals(0, results.size());
        
        q.setStart(2);
        results = registry.search(q).getResults();
        assertEquals(0, results.size());
        
        q = new Query(Artifact.class).add(OpRestriction.eq("phase", "Default:Created"));
        results = registry.search(q).getResults();
    
        assertEquals(1, results.size());

        q = new Query(Artifact.class).add(OpRestriction.eq("lifecycle", "Default"));
        results = registry.search(q).getResults();
    
        assertEquals(1, results.size());
        
        q = new Query(Artifact.class)
            .add(OpRestriction.in("phase", 
                                Arrays.asList(new String[] { "Default:XXXX", "Default:Developed" })));
        results = registry.search(q).getResults();
    
        assertEquals(0, results.size());
        
        q = new Query(Artifact.class)
            .add(OpRestriction.in("lifecycle", 
                                Arrays.asList(new String[] { "Defaul", "notinthisone" })));
        results = registry.search(q).getResults();
    
        assertEquals(1, results.size());
    }

}
