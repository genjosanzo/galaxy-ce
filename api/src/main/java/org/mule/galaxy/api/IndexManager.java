package org.mule.galaxy.api;

import java.util.Collection;

import javax.xml.namespace.QName;

public interface IndexManager {
    
    void save(Index index) throws GalaxyException;

    void save(Index index, boolean blockForIndexing) throws GalaxyException;

    void delete(String id);
    
    Index getIndex(String idxName) throws NotFoundException;

    Collection<Index> getIndexes();
    
    Collection<Index> getIndices(QName documentType);

    void index(ArtifactVersion version);
}
