package org.mule.galaxy.jcr;

import javax.jcr.Node;

import org.mule.galaxy.Registry;

public interface JcrRegistry extends Registry {
    Node getWorkspacesNode();

    Node getIndexNode();
    
    Node getArtifactTypesNode();
}
