package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;

import org.mule.galaxy.Registry;

public interface JcrRegistry extends Registry {
    Node getWorkspacesNode();

    Node getIndexNode();
    
    Node getArtifactTypesNode();
}
