package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.util.Constants;

/**
 * Does some initialization for other artifact types.
 */
public class OtherArtifactPlugin extends AbstractArtifactPlugin implements Constants {

    public void install() throws Exception {
        artifactTypeDao.save(new ArtifactType("Other Artifacts", "*/*"));
    }

    public void initialize() throws Exception {
    }

    public int getVersion() {
        return 1;
    }
    
}
