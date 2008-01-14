/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.test;

import org.mule.galaxy.test.AbstractAtomTest;
import org.mule.galaxy.util.IOUtils;

import java.util.List;
import java.util.Iterator;
import java.io.InputStream;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.axiom.om.util.Base64;

/**
 * Note this test shouldn't be in the main
 */
public abstract class AbstractConfigLookupTest extends AbstractAtomTest
{
    public static final String REGISTRY_URL = "http://localhost:9002/api/registry";

    public void doLookup(String query, int expectedEntries) throws Exception {
        AbderaClient client = new AbderaClient(abdera);


        String search = UrlEncoding.encode(query);
        //String search = UrlEncoding.encode("select artifact where mule2.service = 'GreeterUMO'");
        String url = REGISTRY_URL + "?q=" + search;

        // GET a Feed with Mule Configurations which match the criteria
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        ClientResponse res = client.get(url, defaultOpts);
        assertEquals(200, res.getStatus());

        Document<Feed> feedDoc = res.getDocument();
        prettyPrint(feedDoc);
        List<Entry> entries = feedDoc.getRoot().getEntries();
        assertEquals(expectedEntries, entries.size());
        for (Iterator<Entry> iterator = entries.iterator(); iterator.hasNext();)
        {
            Entry entry =  iterator.next();
            // GET the actual mule configuration
            String urlLink = entry.getContentSrc().toString();
            System.out.println(urlLink);
            res = client.get(urlLink, defaultOpts);
            assertEquals(200, res.getStatus());

            // Use this as your handle to the mule configuration
            InputStream is = res.getInputStream();
//            IOUtils.copy(is, System.out);
            while (is.read() != -1);
            res.release();
            
        }


    }
}