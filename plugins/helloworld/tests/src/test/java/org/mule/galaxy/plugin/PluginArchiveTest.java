package org.mule.galaxy.plugin;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mule.galaxy.test.AbstractAtomTest;
import org.mule.galaxy.web.GwtPlugin;
import org.mule.galaxy.web.GwtRpcHandlerMapping;
import org.mule.galaxy.web.WebManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class PluginArchiveTest extends AbstractAtomTest {
    
    @Override
    protected void setUp() throws Exception {
        new File("./target/galaxy-data").delete();
        super.setUp();
    }

    public void testPluginLoading() throws Exception {
        // Did the GwtPlugin get installed?
        WebManager webManager = (WebManager) getApplicationContext().getBean("webManager");
        Collection<GwtPlugin> plugins = webManager.getGwtPlugins();
        assertEquals(2, plugins.size());
        
        // Are our RPC services actually registered?
        ServletHolder holder = context.getServletHandler().getServlet("handler");
        DispatcherServlet servlet = (DispatcherServlet) holder.getServlet();
        
        WebApplicationContext dispatcherContext = servlet.getWebApplicationContext();
        GwtRpcHandlerMapping handler = (GwtRpcHandlerMapping) dispatcherContext.getBean("gwtHandlerMappings");
        assertNotNull(handler);
        
        Map map = handler.getHandlerMap();
        assertTrue(map.containsKey("/hello.rpc"));

        // Are the web resources actually available?
        HttpClient client = new HttpClient();
        
        client.getParams().setAuthenticationPreemptive(true);
        client.getState().setCredentials(AuthScope.ANY,  new UsernamePasswordCredentials("admin", "admin"));
        
        GetMethod method = new GetMethod("http://localhost:9002/plugins/org.mule.galaxy.helloworld.HelloWorld/org.mule.galaxy.helloworld.HelloWorld.nocache.js");
        client.executeMethod(method);
        assertEquals(200, method.getStatusCode());
    }
}