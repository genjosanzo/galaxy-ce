/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.mule2.config;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationException;
import org.mule.api.lifecycle.LifecycleManager;
import org.mule.config.ConfigResource;
import org.mule.config.builders.AbstractConfigurationBuilder;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.galaxy.config.ConfigurationSupport;
import org.mule.galaxy.config.Resource;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Mule Configuration Builder used for configuring a Mule instance from a Galaxy Registry URL. The can include Login
 * credentials, the location of the server and a query string used to locate artifacts in the registry. For example -
 * <code>
 * http://admin:admin@localhost:9002/api/registry?q=select artifact where mule2.service = 'GreeterUMO'"
 * </code>
 *
 * Note that if the query returns more than one artifact they all get loaded, so its important that the query only returns
 * 'mule' artifacts and only artifacts targeted for this Mule instance.
 */
public class GalaxyConfigurationBuilder extends AbstractConfigurationBuilder
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(GalaxyConfigurationBuilder.class);

    private Properties properties;
    private String url;

    public GalaxyConfigurationBuilder(String url)
    {
        this(url, null);
    }

    public GalaxyConfigurationBuilder(String url, Properties properties)
    {
        this.url = url;
        this.properties = properties;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    protected void doConfigure(MuleContext muleContext) throws Exception
    {
        //We may want to set system props here or access to props in the MuleContext
        if(properties==null) properties = new Properties();
         try
        {
            org.mule.galaxy.config.ConfigurationSupport configSupport = new ConfigurationSupport();
            Resource[] is = configSupport.getArtifacts(url, properties);

            ConfigResource[] resources = new ConfigResource[is.length];
            for (int i = 0; i < is.length; i++)
            {
                //This will cause the same file to be downloaded twice since Spring doesn't allow you to pass in an
                //input stream when creating a context (complains that validation mode cannot be determined). 
                resources[i] = new ConfigResource(is[i].getName(), is[i].getInputStream());
            }
            //Really we should use the AutoConfigBuilder here so we can load scripted Mule instances, but it doesn;t
            //look like its properly implemented yet
            SpringXmlConfigurationBuilder builder = new SpringXmlConfigurationBuilder(resources);
            builder.configure(muleContext);
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }
    }

    protected void applyLifecycle(LifecycleManager arg0) throws Exception {
    }
    
}