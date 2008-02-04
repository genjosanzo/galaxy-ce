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

import org.mule.config.AbstractConfigurationBuilder;
import org.mule.config.ConfigurationException;
import org.mule.umo.UMOManagementContext;

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

    public UMOManagementContext configure(String[] strings, Properties properties) throws ConfigurationException
    {
        //Not implemented for Galaxy 1.0
        return null;
    }

    //TODO: this will work in Mule 2.0.0-RC2
//    protected void doConfigure(UMOManagementContext managementContext, String[] strings) throws Exception
//    {
//        try
//        {
//            ConfigurationSupport configSupport = new ConfigurationSupport();
//            Resource[] resources = configSupport.getArtifacts(strings[0], null);
//
//            for (int i = 0; i < resources.length; i++)
//            {
//                managementContext.configure(new XmlConfiguration(resources[i].getInputStream()));
//            }
//        }
//        catch (IOException e)
//        {
//            throw new ConfigurationException(e);
//        }
//    }

    public boolean isConfigured()
    {
        return true;
    }
}