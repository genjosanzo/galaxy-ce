/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.impl.content;

import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.util.DOMUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * TODO
 */
public class JarContentHander extends AbstractContentHandler
{
    protected MimeType primaryContentType;

    public JarContentHander() throws MimeTypeParseException
    {
        primaryContentType = new MimeType("application/jar");
        supportedContentTypes.add(primaryContentType);
        supportedContentTypes.add(new MimeType("application/zip"));
        supportedTypes.add(JarFile.class);
    }

    public MimeType getContentType(Object o)
    {
        return primaryContentType;
    }

    public String getName(Object o)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object read(InputStream stream, Workspace workspace) throws IOException
    {
        try
        {
            ZipInputStream zipStream = new ZipInputStream(stream);
            ZipFile zipFile = new ZipFile("");

           // zipStream.getNextEntry().
          //  JarFile jar = new JarFile()
            return DOMUtils.readXml(stream);
        }
        catch (SAXException e)
        {
            IOException e2 = new IOException("Could not read XML.");
            e2.initCause(e);
            throw e2;
        }
        catch (ParserConfigurationException e)
        {
            IOException e2 = new IOException("Could not read XML.");
            e2.initCause(e);
            throw e2;
        }
    }

    public void write(Object o, OutputStream stream) throws IOException
    {
            ZipFile zipFile = (ZipFile)o;
            ZipOutputStream zipStream = new ZipOutputStream(stream);
            while (zipFile.entries().hasMoreElements())
            {
                ZipEntry zipEntry = zipFile.entries().nextElement();
                zipStream.putNextEntry(zipEntry);

            }
    }

}
