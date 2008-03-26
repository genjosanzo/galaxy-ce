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

/**
 * TODO
 */
public class AnnotatedArtifactPlugin extends AbstractArtifactPlugin
{
    public AnnotatedArtifactPlugin()
    {
    }

    @Override
    public void doInstall() throws Exception
    {
       // ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
       // resourcePatternResolver.getResources()
    }

    public int getVersion() {
        return 1;
    }
}
