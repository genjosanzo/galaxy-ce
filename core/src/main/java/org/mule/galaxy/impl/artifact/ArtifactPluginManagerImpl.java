/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactPlugin;
import org.mule.galaxy.ArtifactPluginManager;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.IndexManager;
import org.mule.galaxy.Registry;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.plugins.config.jaxb.GalaxyPluginType;
import org.mule.galaxy.plugins.config.jaxb.GalaxyPluginsType;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.view.ViewManager;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

/**
 * TODO
 */
public class ArtifactPluginManagerImpl implements ArtifactPluginManager, ApplicationContextAware
{
    public static final String PLUGIN_SERVICE_PATH = "META-INF/";

    public static final String GALAXY_PLUGIN_DESCRIPTOR = "galaxy-plugins.xml";

    protected Registry registry;
    protected Dao<ArtifactType> artifactTypeDao;
    protected ViewManager viewManager;
    protected IndexManager indexManager;
    protected PolicyManager policyManager;
    private ApplicationContext context;
    private JcrTemplate jcrTemplate;


    public void setApplicationContext(ApplicationContext
            context) throws BeansException
    {
        this.context = context;
    }

    public void setJcrTemplate(JcrTemplate jcrTemplate)
    {
        this.jcrTemplate = jcrTemplate;
    }

    public void setRegistry(Registry registry)
    {
        this.registry = registry;
    }

    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao)
    {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setViewManager(ViewManager viewManager)
    {
        this.viewManager = viewManager;
    }

    public void setIndexManager(IndexManager indexManager)
    {
        this.indexManager = indexManager;
    }

    public void setPolicyManager(PolicyManager policyManager)
    {
        this.policyManager = policyManager;
    }

    public void initialize() throws Exception
    {
        initializeXmPlugins();
        initializeBeanPlugins();
    }

    protected void initializeXmPlugins() throws Exception
    {

        JcrUtil.doInTransaction(jcrTemplate.getSessionFactory(), new JcrCallback()
        {
            public Object doInJcr(Session session) throws IOException, RepositoryException
            {
                try
                {
                    JAXBContext jc = JAXBContext.newInstance("org.mule.galaxy.plugins.config.jaxb");

                    Enumeration e = getClass().getClassLoader().getResources(PLUGIN_SERVICE_PATH + GALAXY_PLUGIN_DESCRIPTOR);
                    while (e.hasMoreElements())
                    {
                        URL url = (URL) e.nextElement();
                        Unmarshaller u = jc.createUnmarshaller();
                        JAXBElement ele = (JAXBElement) u.unmarshal(url.openStream());

                        GalaxyPluginsType pluginsType = (GalaxyPluginsType) ele.getValue();
                        List<GalaxyPluginType> pluginsList = pluginsType.getGalaxyPlugin();

                        for (Iterator<GalaxyPluginType> iterator = pluginsList.iterator(); iterator.hasNext();)
                        {
                            GalaxyPluginType pluginType = iterator.next();

                            ConfigurableArtifactPlugin plugin = new ConfigurableArtifactPlugin(pluginType);
                            plugin.setArtifactTypeDao(artifactTypeDao);
                            plugin.setIndexManager(indexManager);
                            plugin.setRegistry(registry);
                            plugin.setViewManager(viewManager);
                            plugin.setPolicyManager(policyManager);


                            if (System.getProperty("initializeOnce") != null)
                            {
                                plugin.initializeOnce();
                            }
                            plugin.initializeEverytime();
                        }
                    }
                }
                catch (Exception e1)
                {
                    throw new RuntimeException(e1);
                }
                return null;
            }
        });

    }

    protected void initializeBeanPlugins() throws Exception
    {
        JcrUtil.doInTransaction(jcrTemplate.getSessionFactory(), new JcrCallback()
        {

            public Object doInJcr(Session session) throws IOException, RepositoryException
            {
                Map plugins = context.getBeansOfType(ArtifactPlugin.class);
                try
                {
                    if (System.getProperty("initializeOnce") != null)
                    {
                        for (Object o : plugins.values())
                        {
                            ((ArtifactPlugin) o).initializeOnce();
                        }
                    }

                    for (Object o : plugins.values())
                    {
                        ((ArtifactPlugin) o).initializeEverytime();
                    }
                }
                catch (RepositoryException e)
                {
                    throw e;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                return null;
            }

        });
    }
}
