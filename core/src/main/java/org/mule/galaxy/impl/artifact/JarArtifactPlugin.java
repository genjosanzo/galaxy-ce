package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.util.Constants;

import java.util.List;

/**
 * Java Archive (JAR) artifact plugin.
 */
public class JarArtifactPlugin extends AbstractArtifactPlugin implements Constants
{

    public void initializeOnce() throws Exception
    {
        artifactTypeDao.save(new ArtifactType("Java Archives (JARs)", "application/java-archive"));
        System.out.println(">>> Installed JAR plugin");
    }

    public void initializeEverytime() throws Exception
    {
        // TODO figure out a nice way to upgrade, for now just a hack to re-init completely while developing
        List<ArtifactType> jarHandlers = artifactTypeDao.find("contentType", "application/java-archive");

        if (!jarHandlers.isEmpty())
        {
            System.out.printf("Found %d jar handlers, will remove them and re-register ours\n", jarHandlers.size());
            for (ArtifactType handler : jarHandlers)
            {
                artifactTypeDao.delete(handler.getId());
            }
        }

        artifactTypeDao.save(new ArtifactType("Java Archives (JARs)", "application/java-archive"));
        System.out.println(">>> Updated JAR plugin");
    }

}