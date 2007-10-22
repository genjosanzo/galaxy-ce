package org.mule.galaxy;

import java.io.InputStream;
import java.net.URL;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class AbstractGalaxyTest extends AbstractDependencyInjectionSpringContextTests {
    
    protected JackrabbitRepository jcrRepository;
    protected Registry registry;
    protected Settings settings;
    
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
            setDirty();
        }
        super.onTearDown();
    }

    private void clearJcrRepository() {
        try {
            Session session = jcrRepository.login(new SimpleCredentials("username", "password".toCharArray()));
            
            Node node = session.getRootNode();
            
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                Node child = itr.nextNode();
                if (!child.getName().equals("jcr:system")) {
                    child.remove();
                }
            }
            session.save();
            session.logout();
        } catch (PathNotFoundException t) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    
}
