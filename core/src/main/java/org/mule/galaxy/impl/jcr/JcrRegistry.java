package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.api.Registry;

import javax.jcr.Node;

public interface JcrRegistry extends Registry
{
    Node getWorkspacesNode();

    Node getIndexNode();
    
    Node getArtifactTypesNode();
}
