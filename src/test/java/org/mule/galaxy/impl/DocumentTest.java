package org.mule.galaxy.impl;


import java.util.Collection;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.cxf.helpers.DOMUtils;
import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.jcr.JcrArtifact;

import org.w3c.dom.Document;

public class DocumentTest extends AbstractGalaxyTest {
    protected Registry registry;
    
    public void testAddWsdl() throws Exception {
        Document hello = DOMUtils.readXml(getResourceAsStream("/org/mule/galaxy/wsdl/hello.wsdl"));
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace w = workspaces.iterator().next();
        
        Artifact document = registry.createArtifact(w, hello);
        
        assertNotNull(document.getId());
        assertEquals("application/xml", document.getContentType());
        assertNotNull(document.getDocumentType());
        assertEquals("definitions", document.getDocumentType().getLocalPart());
        
        Set<? extends ArtifactVersion> versions = document.getVersions();
        assertNotNull(versions);
        assertEquals(1, versions.size());
        
        ArtifactVersion version = versions.iterator().next();
        assertSame(hello, version.getData());
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
