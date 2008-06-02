package org.mule.galaxy.index;

import java.util.Collection;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.NotFoundException;

public interface IndexManager {
    
    void save(Index index) throws GalaxyException;

    void save(Index index, boolean blockForIndexing) throws GalaxyException;

    void delete(String id, boolean removeArtifactMetadata);
    
    Index getIndex(String idxName) throws NotFoundException;

    Index getIndexByName(final String name) throws NotFoundException;
    
    Collection<Index> getIndexes();
    
    Collection<Index> getIndexes(ArtifactVersion artifactVersion);

    void index(ArtifactVersion version);
}
