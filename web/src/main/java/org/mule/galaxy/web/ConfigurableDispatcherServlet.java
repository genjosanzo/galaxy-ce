package org.mule.galaxy.web;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Allows the user of this servlet to configure the name of the handler mapping in the
 * web.xml file as the <code>handlerMappingName</code> init-param instead of letting
 * {@link DispatcherServlet} go wild finding whatever {@link HandlerMapping}s it can.
 */
public class ConfigurableDispatcherServlet extends DispatcherServlet {
    
    @Override
    protected List getDefaultStrategies(ApplicationContext context, Class strategyInterface)
        throws BeansException {
        
        if (HandlerMapping.class.equals(strategyInterface)) {
            String name = getServletConfig().getInitParameter("handlerMappingName");
            
            try {
                Object ha = context.getBean(name);
                
                return Collections.singletonList(ha);
            }
            catch (NoSuchBeanDefinitionException ex) {
                // Ignore, we'll add a default HandlerAdapter later.
            }
        }
        
        // fall back to default behavior
        return super.getDefaultStrategies(context, strategyInterface);
    }
    
}
