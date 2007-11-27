package org.mule.galaxy;

import org.mule.galaxy.view.ViewManager;

/**
 * Makes it easy to add indexes and views for a new artifact type.
 */
public abstract class AbstractArtifactPlugin {
    protected Registry registry;
    protected Dao<ArtifactType> artifactTypeDao;
    protected ViewManager viewManager;
    
    public abstract void initialize() throws Exception;

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
    
    
}
