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

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * TODO
 */
public class AnnotatedArtifactPlugin extends AbstractArtifactPlugin
{
    public AnnotatedArtifactPlugin()
    {
    }

    public void initializeEverytime() throws Exception
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void initializeOnce() throws Exception
    {
       // ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
       // resourcePatternResolver.getResources()
    }
}
