package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.api.ArtifactPlugin;

import java.io.IOException;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class PluginRunner implements ApplicationContextAware {
    private ApplicationContext context;
    private JcrTemplate jcrTemplate;
    
    public void setJcrTemplate(JcrTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void initialize() throws Exception {
        JcrUtil.doInTransaction(jcrTemplate.getSessionFactory(), new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Map plugins = context.getBeansOfType(ArtifactPlugin.class);
                try {
                    if (System.getProperty("initializeOnce") != null) {
                        for (Object o : plugins.values()) {
                            ((ArtifactPlugin) o).initializeOnce();
                        }
                    }
                    
                    for (Object o : plugins.values()) {
                        ((ArtifactPlugin) o).initializeEverytime();
                    }
                } catch (RepositoryException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
            
        });
    }
}
