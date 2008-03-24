package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.Plugin;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Registry;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.view.ViewManager;

/**
 * Makes it easy to add indexes and views for a new artifact type.
 */
public abstract class AbstractArtifactPlugin implements Plugin {
    protected Registry registry;
    protected Dao<ArtifactType> artifactTypeDao;
    protected ViewManager viewManager;
    protected IndexManager indexManager;

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public String getName() {
        return getClass().getName();
    }
    
}
