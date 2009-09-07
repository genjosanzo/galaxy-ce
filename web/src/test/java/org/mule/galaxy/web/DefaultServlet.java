package org.mule.galaxy.web;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.mortbay.resource.FileResource;
import org.mortbay.resource.Resource;

public class DefaultServlet extends org.mortbay.jetty.servlet.DefaultServlet{

    private List<Resource> other;

    public DefaultServlet() throws MalformedURLException, IOException, URISyntaxException {
        super();
        FileResource eeWebappResource = new FileResource(new File("src/main/webapp").toURI().toURL());
        FileResource eeTestWebappResource = new FileResource(new File("target/gwt-webapp").toURI().toURL());
        other = Arrays.asList((Resource)eeWebappResource, (Resource)eeTestWebappResource);
    }

    @Override
    public Resource getResource(String pathInContext) {
         return getResource(super.getResource(pathInContext), 
                            other, pathInContext);
    }

    public static Resource getResource(final Resource superResource,
                                       final List<Resource> other,
                                       String uriInContext) {
        try {
            if ("/".equals(uriInContext) && superResource != null) {
                return new DelegateResource(superResource, other);
            }
            
            Resource r = null;
            for (Resource eeResource : other) {
                r = eeResource.addPath(uriInContext);
            
                if (r != null && r.exists()) {
                    break;
                }
            }
        
            if (r == null || !r.exists()) {
                r = new DelegateResource(superResource, other);
            }
            
            return r;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}
