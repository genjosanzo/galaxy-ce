package org.mule.galaxy.index;

import java.io.IOException;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;

public interface Indexer {
    void index(ArtifactVersion artifact, 
               ContentHandler contentHandler, 
               Index index) throws IOException, IndexException;
}
