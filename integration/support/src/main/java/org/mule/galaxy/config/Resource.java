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

import java.io.InputStream;

/**
 * A wrapper for an input stream that associates the Artifact name with the stream
 */
public class Resource
{
    private String name;
    private InputStream inputStream;

    public Resource(InputStream inputStream, String name)
    {
        this.inputStream = inputStream;
        this.name = name;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public String getName()
    {
        return name;
    }
}
