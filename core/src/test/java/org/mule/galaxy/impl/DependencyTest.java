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
        
        ArtifactResult portType = registry.createArtifact(workspace, 
                                                          "application/wsdl+xml", 
                                                          "hello-portType.wsdl", 
                                                          "0.1", 
                                                          getResourceAsStream("/wsdl/imports/hello-portType.wsdl"), 
                                                          getAdmin());
        
        ArtifactResult svcWsdl = registry.createArtifact(workspace, 
                                                         "application/wsdl+xml", 
                                                         "hello.wsdl", 
                                                         "0.1", 
                                                         getResourceAsStream("/wsdl/imports/hello.wsdl"), 
                                                         getAdmin());
        
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
