package org.mule.galaxy.web;

import java.io.IOException;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.artifact.AbstractArtifactPlugin;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;

public class DemoArtifactPlugin extends AbstractArtifactPlugin {
    private UserManager userManager;
    
    public void initializeEverytime() throws Exception {
    }

    public void initializeOnce() throws Exception {
        Workspace w = registry.getWorkspaces().iterator().next();
        
        User user = userManager.find("username", "admin").iterator().next();
        
        add(w, user, "hello-config.xml", "/mule/hello-config.xml");
        add(w, user, "hello.xsd", "/wsdl/imports/hello.xsd");
        add(w, user, "hello-portType.wsdl", "/wsdl/imports/hello-portType.wsdl");
        add(w, user, "hello.wsdl", "/wsdl/imports/hello.wsdl");   
    }

    private void add(Workspace w, User user, String name, String resource) 
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException {

        registry.createArtifact(w, 
                                "application/xml", 
                                name, 
                                "0.1", 
                                getClass().getResourceAsStream(resource), 
                                user);
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
