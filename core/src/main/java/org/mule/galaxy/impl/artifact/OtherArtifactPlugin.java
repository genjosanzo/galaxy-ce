package org.mule.galaxy.impl.artifact;

import javax.xml.namespace.QName;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.util.Constants;

/**
 * Does some initialization for other artifact types.
 */
public class OtherArtifactPlugin extends AbstractArtifactPlugin implements Constants {

    public void initializeOnce() throws Exception {
        artifactTypeDao.save(new ArtifactType("Other Artifacts", "*/*"));
    }

    public void initializeEverytime() throws Exception {
    }

    public int getVersion() {
        return 1;
    }
    
}
