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

/**
 * Defines Properties that can be recogined by the Galaxy {@link org.mule.galaxy.config.ConfigurationSupport}
 */
public interface GalaxyProperties
{
    public static final String GALAXY_PREFIX = "galaxy.";
    public static final String PROPERTY_WORKSPACE = GALAXY_PREFIX + "workspace";
    public static final String PROPERTY_USERNAME = GALAXY_PREFIX + "username";
    public static final String PROPERTY_PASSWORD = GALAXY_PREFIX + "password";
    public static final String PROPERTY_QUERY = GALAXY_PREFIX + "query";
}
