package org.mule.galaxy.impl.plugin;

import org.mule.galaxy.artifact.ArtifactType;

/**
 * Does some initialization for other artifact types.
 */
public class OtherArtifactPlugin extends AbstractPlugin
{

    @Override
    public void doInstall() throws Exception {
        artifactTypeDao.save(new ArtifactType("Other Artifacts", "*/*", null));
    }

    public int getVersion() {
        return 1;
    }
    
}
