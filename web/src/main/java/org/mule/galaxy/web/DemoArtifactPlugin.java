package org.mule.galaxy.web;

import org.mule.galaxy.api.ArtifactPolicyException;
import org.mule.galaxy.api.PropertyDescriptor;
import org.mule.galaxy.api.RegistryException;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.security.User;
import org.mule.galaxy.api.security.UserManager;
import org.mule.galaxy.impl.artifact.AbstractArtifactPlugin;

import java.io.IOException;

import javax.activation.MimeTypeParseException;

public class DemoArtifactPlugin extends AbstractArtifactPlugin {
    private UserManager userManager;
    
    public void initializeEverytime() throws Exception {
    }

    public void initializeOnce() throws Exception {
        Workspace w = registry.getWorkspaces().iterator().next();
        
        User user = userManager.find("username", "admin").iterator().next();
        
        add(w, user, "hello-config.xml", "/mule/hello-config.xml");
        add(w, user, "applicationContext.xml", "/spring/test-applicationContext.xml");
        add(w, user, "hello-config-mule2.xml", "/mule2/hello-config.xml");
        add(w, user, "hello.xsd", "/wsdl/imports/hello.xsd");
        add(w, user, "hello-portType.wsdl", "/wsdl/imports/hello-portType.wsdl");
        add(w, user, "hello.wsdl", "/wsdl/imports/hello.wsdl");   
        
        PropertyDescriptor pd = new PropertyDescriptor("location", "Location", false);
        registry.savePropertyDescriptor(pd);
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
