package org.mule.galaxy.impl.jcr;

import static org.mule.galaxy.impl.jcr.JcrUtil.getOrCreate;

import java.io.IOException;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.ArtifactPlugin;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;
import org.springmodules.jcr.jackrabbit.support.UserTxSessionHolder;

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
                    throw (RepositoryException) e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
            
        });
    }
}
