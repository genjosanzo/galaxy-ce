package org.mule.galaxy.impl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.index.XPathIndexer;
import org.mule.galaxy.impl.index.XQueryIndexer;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class IndexTest extends AbstractGalaxyTest {

    public void testXmlSchema() throws Exception {
        Artifact a = importXmlSchema();
        
        assertEquals("http://www.example.org/test/", 
                     a.getProperty("xmlschema.targetNamespace"));
        
        Object property = a.getProperty("xmlschema.element");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testElement"));

        property = a.getProperty("xmlschema.complexType");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testComplexType"));

        property = a.getProperty("xmlschema.group");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testGroup"));

        property = a.getProperty("xmlschema.attributeGroup");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        assertTrue(((Collection) property).contains("testAttributeGroup"));
        
        
    }
    public void xtestReindex() throws Exception {
        Artifact artifact = importHelloWsdl();
        
        Index i = indexManager.getIndex("wsdl.targetNamespace");
        
        i.getConfiguration().put(XPathIndexer.XPATH_EXPRESSION, "concat('foo', 'bar')");
        
        indexManager.save(i);
        
        Thread.sleep(2000);
        
        artifact = (Artifact) registry.getItemById(artifact.getId());
        Object value = artifact.getProperty("wsdl.targetNamespace");
        assertEquals("foobar", value);
    }
    
    public void testDelete() throws Exception {
        Artifact artifact = importHelloWsdl();
        
        Collection<Index> indices = indexManager.getIndexes();
        assertNotNull(indices);
        
        Index tnsIdx = null;
        Index ptIdx = null;
        Index svcIdx = null;
        for (Index i : indices) {
            String prop = i.getConfiguration().get(XQueryIndexer.PROPERTY_NAME);
            if (prop == null) {
                continue;
            }
            
            if (prop.equals("wsdl.targetNamespace")) {
                tnsIdx = i;
            } else if (prop.equals("wsdl.endpoint")) {
                ptIdx = i;
            } else if (prop.equals("wsdl.service")) {
                svcIdx = i;
            }
        }
        
        assertNotNull(tnsIdx);
        assertNotNull(ptIdx);
        assertNotNull(svcIdx);
        
        indexManager.delete(tnsIdx.getId(), true);
        
        artifact = (Artifact) registry.getItemById(artifact.getId());
        Object value = artifact.getProperty("wsdl.targetNamespace");
        assertNull(value);
        assertNull(artifact.getPropertyInfo("wsdl.targetNamespace"));

        indexManager.delete(ptIdx.getId(), false);
        
        artifact = (Artifact) registry.getItemById(artifact.getId());
        value = artifact.getProperty("wsdl.endpoint");
        assertNotNull(value);
        
        indexManager.delete(svcIdx.getId(), true);
        
        artifact = (Artifact) registry.getItemById(artifact.getId());
        value = artifact.getProperty("wsdl.service");
        assertNull(value);
        assertNull(artifact.getPropertyInfo("wsdl.service"));
    }
    
    
    public void testWsdlIndex() throws Exception {
        Collection<Index> indices = indexManager.getIndexes();
        assertNotNull(indices);
//        assertEquals(5, indices.size());
        
        Index idx = null;
        Index tnsIdx = null;
        for (Index i : indices) {
            String prop = i.getConfiguration().get(XQueryIndexer.PROPERTY_NAME);
            if (prop == null) {
                continue;
            }
            
            if (prop.equals("wsdl.service")) {
                idx = i;
            } else if (prop.equals("wsdl.targetNamespace")) {
                tnsIdx = i;
            }
        }
        
        assertEquals("xquery", idx.getIndexer());
        assertEquals(String.class, idx.getQueryType());
        assertNotNull(idx.getConfiguration());
        assertEquals("wsdl.service", idx.getConfiguration().get(XQueryIndexer.PROPERTY_NAME));
        assertNotNull(idx.getConfiguration().get(XQueryIndexer.XQUERY_EXPRESSION));
        assertEquals(1, idx.getDocumentTypes().size());
        
        assertNotNull(tnsIdx.getId());
        assertEquals("xpath", tnsIdx.getIndexer());
        assertEquals(String.class, tnsIdx.getQueryType());
        assertNotNull(tnsIdx.getConfiguration());
        assertEquals("wsdl.targetNamespace", tnsIdx.getConfiguration().get(XQueryIndexer.PROPERTY_NAME));
        assertNotNull(tnsIdx.getConfiguration().get(XPathIndexer.XPATH_EXPRESSION));
        assertEquals(1, tnsIdx.getDocumentTypes().size());
        
        // Import a document which should now be indexed
        Artifact artifact = importHelloWsdl();

        EntryVersion version = artifact.getDefaultOrLastVersion();
        Object property = version.getProperty("wsdl.service");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection services = (Collection) property;
        
        assertTrue(services.contains("HelloWorldService"));
        
        property = version.getProperty("wsdl.targetNamespace");
        assertNotNull(property);
        assertEquals("http://mule.org/hello_world", property);
        
        property = version.getProperty("wsdl.endpoint");
        assertNotNull(property);
        assertTrue(property instanceof Collection);
        Collection endpoints = (Collection) property;
        assertTrue(endpoints.contains("SoapPort"));

        Collection ptDeps = (Collection) version.getProperty("wsdl.portType.dependencies");
        assertNotNull(ptDeps);
        QName q = (QName) ptDeps.iterator().next();
        assertEquals(new QName("http://mule.org/hello_world", "HelloWorld"), q);
        
        Collection bDeps = (Collection) version.getProperty("wsdl.binding.dependencies");
        assertNotNull(bDeps);
        q = (QName) bDeps.iterator().next();
        assertEquals(new QName("http://mule.org/hello_world", "HelloWorldBinding"), q);
        
        // Try out search!
        Set results = registry.search("select artifact where wsdl.service = 'HelloWorldService'", 0, 100).getResults();
        assertEquals(1, results.size());
        
        Artifact next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(Artifact.class, 
                                                OpRestriction.eq("wsdl.service", 
                                                               new QName("HelloWorldService")))).getResults();
        
        assertEquals(1, results.size());
        
        next = (Artifact) results.iterator().next();
        assertEquals(1, next.getVersions().size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            OpRestriction.eq("wsdl.service", 
                                                           new QName("HelloWorldService")))).getResults();
    
        assertEquals(1, results.size());
        
        ArtifactVersion nextAV = (ArtifactVersion) results.iterator().next();
        assertEquals("0.1", nextAV.getVersionLabel());
        // assertNotNull(nextAV.getData());
        // TODO test data
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            OpRestriction.eq("documentType", Constants.WSDL_DEFINITION_QNAME.toString()))).getResults();
    
        assertEquals(1, results.size());
        
        results = registry.search(new Query(ArtifactVersion.class, 
                                            OpRestriction.eq("contentType", "application/xml"))).getResults();
    
        assertEquals(1, results.size());
        

        results = registry.search(new Query(ArtifactVersion.class, 
                                            OpRestriction.in("contentType", Arrays.asList("application/xml")))).getResults();
    
        assertEquals(1, results.size());
    }

    @SuppressWarnings("unchecked")
    public void testJarIndex() throws Exception
    {
        InputStream stream = getResourceAsStream("test.jar");

        assertNotNull(stream);

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        EntryResult ar = workspace.createArtifact("application/java-archive",
                                                     "test.jar",
                                                     "1",
                                                     stream,
                                                     getAdmin());

        Artifact artifact = (Artifact) ar.getEntry();

        assertNotNull(artifact);

        Collection<Index> indexes = indexManager.getIndexes();
        Index idx = null;
        for (Index i : indexes) {
            if ("JAR Indexes".equals(i.getDescription())) {
                idx = i;
            }
        }
        assertNotNull(idx);

        Map<String, String> indexConfig = idx.getConfiguration();
        assertNotNull(indexConfig);
        assertFalse(indexConfig.isEmpty());
        String scriptSource = indexConfig.get("scriptSource");
        assertEquals("Wrong configuration saved to the JCR repo", "JarIndex.groovy", scriptSource);

        EntryVersion latest = artifact.getDefaultOrLastVersion();

        assertEquals(false, latest.getPropertyInfo("jar.entries").isVisible());
        // normal manifest property
        assertEquals("andrew", latest.getProperty("jar.manifest.Built-By"));
        // OSGi property
        final List<String> pkgs = (List<String>) latest.getProperty("jar.osgi.Export-Package.packages");
        assertNotNull(pkgs);
        assertFalse(pkgs.isEmpty());
        assertTrue(pkgs.contains("org.mule.api"));

        final List<String> entries = (List<String>) latest.getProperty("jar.entries");
        assertNotNull(entries);
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains("org.mule.api.MuleContext"));

        // check that wrong name isn't there, it should be jar.entries instead
        List e = (List) latest.getProperty("jar.manifest.entries");
        assertNull(e);

    }

    @SuppressWarnings("unchecked")
    public void testJavaAnnotationsIndex() throws Exception
    {
        // a compiled java class, but without any package hierarchy, so it can't be
        // properly loaded by a Java classloader
        InputStream stream = getResourceAsStream("annotations_as_bytecode.jar");

        assertNotNull(stream);

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        EntryResult ar = workspace.createArtifact("application/java-archive",
                                                     "test.jar",
                                                     "1",
                                                     stream,
                                                     getAdmin());

        Artifact artifact = (Artifact) ar.getEntry();

        assertNotNull(artifact);

        EntryVersion latest = artifact.getDefaultOrLastVersion();

        // class
        List<String> annotations = (List<String>) latest.getProperty("java.annotations.level.class");
        assertNotNull(annotations);
        assertFalse(annotations.isEmpty());
        assertEquals("org.mule.galaxy.impl.index.annotations.Marker(value=ClassLevel)", annotations.get(0));

        // just check for property existance for other levels, annotation parsing is checked in AsmAnnotationsScannerTest

        // field
        annotations = (List<String>) latest.getProperty("java.annotations.level.field");
        assertNotNull(annotations);
        assertFalse(annotations.isEmpty());

        // method
        annotations = (List<String>) latest.getProperty("java.annotations.level.method");
        assertNotNull(annotations);
        assertFalse(annotations.isEmpty());

        // param
        annotations = (List<String>) latest.getProperty("java.annotations.level.param");
        assertNotNull(annotations);
        assertFalse(annotations.isEmpty());
    }
}
