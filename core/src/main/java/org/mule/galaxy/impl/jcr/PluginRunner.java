package org.mule.galaxy.impl.jcr;

import java.util.Map;

import javax.jcr.Session;

import org.mule.galaxy.ArtifactPlugin;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
        Session session = jcrTemplate.getSessionFactory().getSession();
        UserTxSessionHolder sessionHolder = new UserTxSessionHolder(session);
        TransactionSynchronizationManager.bindResource(jcrTemplate.getSessionFactory(), 
                                                       sessionHolder);
        Map plugins = context.getBeansOfType(ArtifactPlugin.class);
        if (System.getProperty("initializeOnce") != null) {
            for (Object o : plugins.values()) {
                ((ArtifactPlugin) o).initializeOnce();
            }
        }
        
        for (Object o : plugins.values()) {
            ((ArtifactPlugin) o).initializeEverytime();
        }
        TransactionSynchronizationManager.unbindResource(jcrTemplate.getSessionFactory());
    }
}
