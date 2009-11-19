package org.mule.galaxy.web;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Map;

import javax.servlet.ServletContext;

import org.gwtwidgets.server.spring.RPCServiceExporter;
import org.gwtwidgets.server.spring.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

public class GwtPluginPostProcessor 
    implements BeanPostProcessor, ApplicationContextAware, ServletContextAware {

    private ApplicationContext applicationContext;
    private WebManager webManager;
    private GwtRpcHandlerMapping gwtHandler;
    private ServletContext servletContext;
    boolean noweb = false;
    
    public GwtPluginPostProcessor() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (noweb) return bean;
        
        if (webManager == null) {
            try {
                webManager = (WebManager) applicationContext.getBean("webManager");
            } catch (NoSuchBeanDefinitionException e) {
                noweb = true;
                return bean;
            }
        }
        
        if (gwtHandler == null) {
            gwtHandler = (GwtRpcHandlerMapping) applicationContext.getBean("gwtHandlerMappings");
        }
        
        if (servletContext == null) {
            throw new RuntimeException("ServletContext cannot be null!");
        }
        
        if (bean instanceof GwtModule) {
            GwtModule plugin = (GwtModule) bean;
            
            webManager.addGwtModule(plugin);
            
            Map<String, RemoteService> services = plugin.getRpcServices();
            if (services != null) {
	            for (Map.Entry<String,RemoteService> e : services.entrySet()) {
	                // create a spring service which gets mapped to the specified URL
	                RPCServiceExporter exporter = new GwtRpcServiceExporter(Thread.currentThread().getContextClassLoader());
	                exporter.setResponseCachingDisabled(false);
	                exporter.setServletContext(servletContext);
	                exporter.setService(e.getValue());
	                exporter.setServiceInterfaces(ReflectionUtils.getExposedInterfaces(e.getValue().getClass()));
	                try {
	                    exporter.afterPropertiesSet();
	                    gwtHandler.registerHandler(e.getKey(), exporter);
	                } catch (Exception ex) {
	                    throw new RuntimeException(ex);
	                }
	            }
            }
        } else if (bean instanceof GwtFacet) {
        	webManager.addGwtFacet((GwtFacet) bean);
        }
        
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
