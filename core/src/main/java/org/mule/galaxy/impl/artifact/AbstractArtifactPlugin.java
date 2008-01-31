package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.api.ArtifactPlugin;
import org.mule.galaxy.api.Dao;
import org.mule.galaxy.api.IndexManager;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.artifact.ArtifactType;
import org.mule.galaxy.api.view.ViewManager;

/**
 * Makes it easy to add indexes and views for a new artifact type.
 */
public abstract class AbstractArtifactPlugin implements ArtifactPlugin {
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
    
}
