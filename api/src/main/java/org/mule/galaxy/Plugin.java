package org.mule.galaxy;

public interface Plugin {

    void initializeOnce() throws Exception;
    void initializeEverytime() throws Exception;
    void setRegistry(Registry r);

    String getName();
    int getVersion();    
}
