package org.mule.galaxy.query;

import java.util.List;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;

public abstract class AbstractFunction {
    
    public abstract String getModule();
    
    public abstract String getName();
    
    public abstract void modifyArtifacts(Object[] args, Set<Artifact> artifacts);

    public abstract void modifyArtifactVersions(Object[] args, Set<ArtifactVersion> artifacts);

    /**
     * Add any possible filters to narrow down the list of artifacts
     * returned. 
     * @param query
     */
    public List<OpRestriction> getRestrictions(Object[] arguments) {
        return null;
    }
}