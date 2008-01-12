/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.config;

import org.mule.config.ConfigurationBuilder;
import org.mule.config.ConfigurationException;
import org.mule.config.ReaderResource;
import org.mule.config.builders.MuleXmlConfigurationBuilder;
import org.mule.umo.manager.UMOManager;
import org.mule.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Mule Configuration Builder used for configuring a Mule instance from a Galaxy Registry URL. The can include Login
 * credentials, the location of the server and a query string used to locate artifacts in the registry. For example -
 * <code>
 * http://admin:admin@localhost:9002/api/registry?q=select artifact where mule.service = 'GreeterUMO'"
 * </code>
 *
 * Note that if the query returns more than one artifact they all get loaded, so its important that the query only returns
 * 'mule' artifacts and only artifacts targeted for this Mule instance.
 */
public class GalaxyConfigurationBuilder implements ConfigurationBuilder
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(GalaxyConfigurationBuilder.class);


    public UMOManager configure(ReaderResource[] readerResources, Properties properties) throws ConfigurationException
    {
        throw new UnsupportedOperationException("configure(ReaderResource[], props)");
    }

    public UMOManager configure(String s) throws ConfigurationException
    {
        return configure(s, null);
    }

    public UMOManager configure(String url, String props) throws ConfigurationException
    {
        Properties properties = new Properties();

        try
        {
            if (props != null)
            {
                properties.load(ClassUtils.getResource(props, getClass()).openStream());
            }
            ConfigurationSupport configSupport = new ConfigurationSupport();
            InputStream[] is = configSupport.getArtifacts(url, properties);

            ReaderResource[] resources = new ReaderResource[is.length];
            for (int i = 0; i < is.length; i++)
            {
                resources[i] = new ReaderResource(url, new InputStreamReader(is[i]));
            }
            MuleXmlConfigurationBuilder builder = new MuleXmlConfigurationBuilder();
            return builder.configure(resources, properties);
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }
    }


    public boolean isConfigured()
    {
        return true;
    }
}
