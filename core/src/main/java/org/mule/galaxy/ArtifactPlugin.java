package org.mule.galaxy;

/**
 * NOTE: this class will most likely change in the future, and is just temporary for now.
 */
public interface ArtifactPlugin {

    void initializeOnce() throws Exception;
    void initializeEverytime() throws Exception;
    void setRegistry(Registry r);
}
