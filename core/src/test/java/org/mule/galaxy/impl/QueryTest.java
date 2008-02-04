package org.mule.galaxy.impl;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

import java.util.Set;

public class QueryTest extends AbstractGalaxyTest {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
        
        
    }
    
    public void test() throws Exception
    {
        
    }
    
//    public void testQueries() throws Exception {
//        // Import a document which should now be indexed
//        importHelloWsdl();
//
//        // Import a document which should now be indexed
//        Artifact muleArtifact = importHelloMule();
//
//        Workspace workspace = muleArtifact.getWorkspace();
//
//        // Try out search!
//        Set results = registry.search(new Query(Artifact.class).workspaceId(workspace.getId())).getResults();
//
//        assertEquals(2, results.size());
//
//        results = registry.search(new Query(Artifact.class)
//            .workspaceId(workspace.getId())
//                 .add(Restriction.eq("mule2.service", "GreeterUMO"))
//                 .add(Restriction.eq("documentType", Constants.MULE2_QNAME))).getResults();
//        assertEquals(1, results.size());
//
//        results = registry.search(new Query(Artifact.class)
//            .workspacePath(workspace.getPath())
//                 .add(Restriction.like("mule2.service", "Greeter"))).getResults();
//        assertEquals(1, results.size());
//
//        results = registry.search("select artifact where mule2.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
//        assertEquals(1, results.size());
//
//        results = registry.search("select artifact where mule2.service in ('GreeterUMO')", 0, 100).getResults();
//        assertEquals(1, results.size());
//
//        results = registry.search("select artifact where mule2.service in ('Bleh')", 0, 100).getResults();
//        assertEquals(0, results.size());
//
//        results = registry.search("select artifact from '/Default Workspace' where mule2.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
//        assertEquals(1, results.size());
//
//        results = registry.search("select artifact from '/Foo' where mule2.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
//        assertEquals(0, results.size());
//
//
//        results = registry.search(new Query(Artifact.class)
//            .workspaceId(workspace.getId())
//                 .add(Restriction.not(Restriction.eq("documentType", Constants.MULE2_QNAME)))).getResults();
//        assertEquals(1, results.size());
//
//        results = registry.search(new Query(Artifact.class)
//            .workspaceId(workspace.getId())
//                 .add(Restriction.like("mule2.service", "Greeter"))).getResults();
//        assertEquals(1, results.size());
//
//    }

}
