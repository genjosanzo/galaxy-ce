package org.mule.galaxy.impl;


import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.Node;
import javax.wsdl.Definition;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Dependency;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class DependencyTest extends AbstractGalaxyTest {
    
    public void testAddDependencies() throws Exception {

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult schema = registry.createArtifact(workspace, 
                                                        "application/xml", 
                                                        "hello.xsd", 
                                                        "0.1", 
                                                        getResourceAsStream("/wsdl/imports/hello.xsd"), 
                                                        getAdmin());
        
        Set<Dependency> deps = schema.getArtifactVersion().getDependencies();
        assertEquals(0, deps.size());
        
        ArtifactResult portType = registry.createArtifact(workspace, 
                                                          "application/wsdl+xml", 
                                                          "hello-portType.wsdl", 
                                                          "0.1", 
                                                          getResourceAsStream("/wsdl/imports/hello-portType.wsdl"), 
                                                          getAdmin());
        deps = portType.getArtifactVersion().getDependencies();
        assertEquals(1, deps.size());
        
        Dependency dep = deps.iterator().next();
        assertEquals(schema.getArtifact().getId(), dep.getArtifact().getId());
        assertFalse(dep.isUserSpecified());
        
        ArtifactResult svcWsdl = registry.createArtifact(workspace, 
                                                         "application/wsdl+xml", 
                                                         "hello.wsdl", 
                                                         "0.1", 
                                                         getResourceAsStream("/wsdl/imports/hello.wsdl"), 
                                                         getAdmin());
        deps = svcWsdl.getArtifactVersion().getDependencies();
        assertEquals(1, deps.size());
        
        dep = deps.iterator().next();
        assertEquals(portType.getArtifact().getId(), dep.getArtifact().getId());
        assertFalse(dep.isUserSpecified());
        
    }
    
    
    public void testMissingDependencies() throws Exception {

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactResult svcWsdl = registry.createArtifact(workspace, 
                                                         "application/wsdl+xml", 
                                                         "hello.wsdl", 
                                                         "0.1", 
                                                         getResourceAsStream("/wsdl/imports/hello-missing.wsdl"), 
                                                         getAdmin());
        
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
