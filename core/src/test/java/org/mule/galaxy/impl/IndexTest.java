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
    public void testIndex() throws Exception {
        /**
         * Creates a document like:
         * <values>
         * <value>{http://acme.com}EchoService</value>
         * <value>{http://acme.com}EchoService2</value>
         * </values>
         * etc.
         */
        String exp = 
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "for $svc in $document//wsdl:service\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "";
       
        registry.registerIndex("wsdl.service", // index field name
                               "WSDL Service", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION); // document QName which this applies to
                
        Set<Index> indices = registry.getIndices(Constants.WSDL_DEFINITION);
        assertNotNull(indices);
        assertEquals(1, indices.size());
        
        Index idx = indices.iterator().next();
        assertEquals("wsdl.service", idx.getId());
        assertEquals("WSDL Service", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(QName.class, idx.getQueryType());
        assertEquals(exp, idx.getExpression());
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
        Set<Artifact> results = registry.search(new Query(ArtifactVersion.class, 
                                                          Restriction.eq("artifactVersion.wsdl.service", 
                                                                         new QName("HelloWorldService"))));
        
        assertEquals(1, results.size());
        
        Artifact next = results.iterator().next();
        assertEquals(1, next.getVersions().size());
    }
}
