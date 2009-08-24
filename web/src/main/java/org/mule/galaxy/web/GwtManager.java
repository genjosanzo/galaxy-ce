package org.mule.galaxy.web;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class GwtManager implements ApplicationContextAware {
    private final Log log = LogFactory.getLog(getClass());

    private Map modules;
    private Map<String,RemoteService> services = new HashMap<String,RemoteService>();
    
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        modules = ctx.getBeansOfType(GwtPlugin.class);
        
        for (Object o : modules.values()) {
            GwtPlugin mod = (GwtPlugin) o;
            log.info("Found GWT module: " + mod.getModuleName());
            
            if (mod.getRpcServices() != null) {
                services.putAll(mod.getRpcServices());
            }
        }
    }

    public Collection<GwtPlugin> getGwtPlugins() {
        return (Collection<GwtPlugin>)modules.values();
    }

    public Map<String,RemoteService> getRpcServices() {
        return services;
    }
}
