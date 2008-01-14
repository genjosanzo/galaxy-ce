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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class ConfigurationSupport
{
    public static final String OPTION_BASIC_AUTHORISATION = "Basic";

    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(ConfigurationSupport.class);

    AbderaClient client = new AbderaClient(new Abdera());

    public Resource[] getArtifacts(String url) throws IOException
    {
        return getArtifacts(url, new Properties());
    }

    public Resource[] getArtifacts(String url, Properties properties) throws IOException
    {
        URL regUrl;
        RequestOptions opts = client.getDefaultRequestOptions();
        regUrl = new URL(url);

        if(properties==null) properties = new Properties();

        getRequestOptionsFromURL(regUrl, opts);
        getRequestOptionsFromProperties(properties, opts);


        String newUrl = regUrl.getProtocol() + "://" + regUrl.getHost() + ":" + regUrl.getPort() + regUrl.getPath();
        String query = regUrl.getQuery();

        if(query==null)
        {
            query = properties.getProperty(GalaxyProperties.PROPERTY_QUERY, null);
            if(query==null)
            {
                throw new IllegalArgumentException("No query was set in the properties or on the server URL");
            }
        }
        query = query.replaceAll("q=", "");
        
        query = UrlEncoding.encode(query);
        newUrl += "?q=" + query;

        ClientResponse res = client.get(newUrl, opts);
        if (res.getStatus() == 200)
        {
            Document<Feed> feedDoc = res.getDocument();
            List<Entry> entries = feedDoc.getRoot().getEntries();
            if (entries.size() == 0)
            {
                throw new IOException("No entries found for request: " + url);
            }

            Resource[] artifacts = new Resource[entries.size()];
            int i=0;
            for (Iterator<Entry> entryIterator = entries.iterator(); entryIterator.hasNext();i++)
            {
                Entry entry = entryIterator.next();
                // GET the actual artifact doc
                String artifactUrlLink = entry.getContentSrc().toString();
                res = client.get(artifactUrlLink, opts);
                if (res.getStatus() == 200)
                {
                    artifacts[i] = new Resource(res.getInputStream(), artifactUrlLink);
                }
                else
                {
                    throw new IOException("Failed to read config from Registry, Status was: " +
                            res.getStatus() + ", " + res.getStatusText());
                }
            }
            return artifacts;
        }
        else
        {
            throw new IOException("Failed to read config from Registry, Status was: " +
                    res.getStatus() + ", " + res.getStatusText());
        }
    }


    protected RequestOptions getRequestOptionsFromProperties(Properties properties, RequestOptions opts)
    {
        String authority = null;
        if(properties==null)
        {
            return opts;
        }
        String user = getOptionalProperty(properties, GalaxyProperties.PROPERTY_USERNAME, null);
        String pass = getOptionalProperty(properties, GalaxyProperties.PROPERTY_PASSWORD, null);
        if (user != null && pass != null)
        {
            authority = user + ":" + pass;
        }
        if (authority != null)
        {
            opts.setAuthorization(OPTION_BASIC_AUTHORISATION + " " + Base64.encode(authority.getBytes()));
        }

        return opts;
    }

    protected RequestOptions getRequestOptionsFromURL(URL regUrl, RequestOptions opts)
    {
        if (regUrl.getUserInfo() != null)
        {
            opts.setAuthorization(OPTION_BASIC_AUTHORISATION + " " + Base64.encode(regUrl.getUserInfo().getBytes()));
        }
        else
        {
            logger.warn("No security credentials set for accessing the registry.");
        }
        return opts;
    }

    /**
     * Retrieves a property with the given name. This method will first try the local properties passed into this
     * configuration builder, then it will try the System.properties() and finally the defaultValue will be used
     *
     * @param props        the properties pased into this config builder
     * @param name         the name of the property to look for
     * @param defaultValue the value to use if the property is not found in the local properties or System properties
     * @return the value of the property or the defaultValue
     */
    protected String getOptionalProperty(Properties props, String name, String defaultValue)
    {
        return props.getProperty(name, System.getProperty(name, defaultValue));
    }

    /**
     * Will look up a property with the given nem in the local properties and System properties
     *
     * @param props the local properties
     * @param name  the name of the property
     * @return the value of the property
     * @throws IllegalArgumentException if the property is not found
     */
    protected String getRequiredProperty(Properties props, String name)
    {
        String value = props.getProperty(name, System.getProperty(name, null));
        if (value == null)
        {
            throw new IllegalArgumentException("Property not found: " + name);
        }
        return value;
    }
}
