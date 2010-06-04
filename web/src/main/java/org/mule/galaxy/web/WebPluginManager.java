package org.mule.galaxy.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.mule.galaxy.impl.plugin.PluginManagerImpl;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class WebPluginManager extends PluginManagerImpl implements ServletContextAware {

    private ServletContext servletContext;
    private static final String PLUGIN_DIRECTORY = "WEB-INF/plugins";
    private static List<File> pluginLocations = new ArrayList<File>();

    /**
     * @return plugin directory location
     */
    protected final String extractPluginDirectory() {
        //ServletContext#getRealPath does not work (by specification) with non-exploded wars.
        //Some containers(WLS) strictly conform to this.
        //Keep its usage first for backward compatibility.
        //Workarounded for WLS using a specific configuration in weblogic.xml
        return servletContext.getRealPath(WebPluginManager.PLUGIN_DIRECTORY);
    }

    @Override
    public void initialize() throws IOException {
        setPluginDirectory(extractPluginDirectory());

        super.initialize();
    }

    @Override
    public void loadPluginDirectory(File directory) throws IOException {
        super.loadPluginDirectory(directory);
        
        addPluginLocation(new File(directory, "web"));
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected ConfigurableApplicationContext createPluginApplicationContext(ApplicationContext context) {
        GenericWebApplicationContext ctx = new GenericWebApplicationContext();
        ctx.setParent(context);
        ctx.setServletContext(servletContext);
        
        GwtPluginPostProcessor processor = new GwtPluginPostProcessor();
        processor.setApplicationContext(context);
        processor.setServletContext(servletContext);
        ((AbstractAutowireCapableBeanFactory)ctx.getAutowireCapableBeanFactory()).addBeanPostProcessor(processor);
        
        return ctx;
    }
    
    public static void addPluginLocation(File location) {
        pluginLocations.add(location);
    }
    
    public static List<File> getPluginLocations() {
        return pluginLocations;
    }
}
