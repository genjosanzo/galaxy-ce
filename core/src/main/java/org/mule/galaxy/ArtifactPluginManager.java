/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy;

import org.mule.galaxy.view.ViewManager;
import org.mule.galaxy.policy.PolicyManager;

/**
 * TODO
 */

public interface ArtifactPluginManager
{
    public void setRegistry(Registry registry);
    
    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao);

    public void setViewManager(ViewManager viewManager);

    public void setIndexManager(IndexManager indexManager);

    public void setPolicyManager(PolicyManager policyManager);    
}
