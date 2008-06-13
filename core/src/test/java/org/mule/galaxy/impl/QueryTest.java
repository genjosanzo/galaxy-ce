package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.test.AbstractGalaxyTest;
import static org.mule.galaxy.query.OpRestriction.not;
import static org.mule.galaxy.query.OpRestriction.eq;
import static org.mule.galaxy.query.OpRestriction.like;

public class QueryTest extends AbstractGalaxyTest {
    
    public void testToString() throws Exception {
        Query q = new Query(Artifact.class)
                .add(eq("phase",  "Default:Created"));
        
        assertEquals("select artifact where phase = 'Default:Created'",
                     q.toString());
        
        q = new Query(Artifact.class)
            .add(not(eq("name",  "foo")));
    
        assertEquals("select artifact where name != 'foo'",
                 q.toString());

        q = new Query(Artifact.class)
            .add(like("name",  "foo"));
    
        assertEquals("select artifact where name like 'foo'",
                 q.toString());

        q = new Query(Artifact.class)
            .add(like("name", "foo"))
            .add(eq("phase", "bar"));
    
        assertEquals("select artifact where name like 'foo' and phase = 'bar'",
                 q.toString());
        
        q = new Query(Artifact.class)
            .workspacePath("/foo", false)
            .add(eq("phase", "bar"));

        assertEquals("select artifact from '/foo' where phase = 'bar'",
             q.toString());
        
        q = new Query(Artifact.class)
            .workspacePath("/foo", true)
            .add(eq("phase", "bar"));

        assertEquals("select artifact from '/foo' recursive where phase = 'bar'",
             q.toString());
        
        q = new Query(Artifact.class)
            .workspaceId("123", true)
            .add(eq("phase", "bar"));

        assertEquals("select artifact from '@123' recursive where phase = 'bar'",
             q.toString());
    }
    
    public void testFromString() throws Exception {
        Query q = Query.fromString("select artifact where name != 'foo'");

        assertEquals(1, q.getRestrictions().size());
        
        OpRestriction opr = (OpRestriction) q.getRestrictions().iterator().next();
        
        assertEquals(Operator.NOT, opr.getOperator());
        
        q = Query.fromString("select artifact from '/foo' recursive");
        
        assertTrue(q.isWorkspaceSearchRecursive());
        assertEquals("/foo", q.getWorkspacePath());
        
        q = Query.fromString("select artifact from '@123' recursive");
        
        assertTrue(q.isWorkspaceSearchRecursive());
        assertEquals("123", q.getWorkspaceId());
    }
    
    public void testQueries() throws Exception {
        Artifact artifact = importXmlSchema();

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

        // search by version
        q = new Query(Artifact.class)
            .add(OpRestriction.eq("version", artifact.getDefaultVersion().getVersionLabel()));
        
        results = registry.search(q).getResults();
    
        assertEquals(1, results.size());
        
        // search by lifecycle
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
                                Arrays.asList(new String[] { "Default", "notinthisone" })));
        results = registry.search(q).getResults();
    
        assertEquals(1, results.size());
    }

}
