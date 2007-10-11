package org.mule.galaxy;

import java.io.InputStream;
import java.net.URL;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class AbstractGalaxyTest extends AbstractDependencyInjectionSpringContextTests {
    
    protected JackrabbitRepository jcrRepository;
    
    public AbstractGalaxyTest() {
        super();
        setPopulateProtectedVariables(true);
    }

    public URL getResource(String name) {
        return getClass().getResource(name);
    }
    
    public InputStream getResourceAsStream(String name) {
        return getClass().getResourceAsStream(name);
    }

    @Override
    protected void onTearDown() throws Exception {
        if (jcrRepository != null) {
            clearJcrRepository();
        }
        super.onTearDown();
    }

    private void clearJcrRepository() {
        try {
            Session session = jcrRepository.login(new SimpleCredentials("username", "password".toCharArray()));
            
//            for (NodeIterator nodes = session.getRootNode().getNodes(); nodes.hasNext();) {
//                System.out.println(nodes.nextNode().getName());
//            }
            Node node = session.getRootNode().getNode("Default Workspace");
            node.remove();
            session.save();
            session.logout();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    
}
