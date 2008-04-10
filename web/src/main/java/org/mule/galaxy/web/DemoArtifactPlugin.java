package org.mule.galaxy.web;

import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.artifact.AbstractArtifactPlugin;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;

public class DemoArtifactPlugin extends AbstractArtifactPlugin {
    private UserManager userManager;
    
    @Override
    public void doInstall() throws Exception {
        Workspace w = registry.getWorkspaces().iterator().next();
        
        User user = userManager.getByUsername("admin");
        
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
        throws Exception {

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

    public int getVersion() {
        return 1;
    }

}
