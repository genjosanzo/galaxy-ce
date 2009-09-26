package org.mule.galaxy.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PluginManager {
    List<PluginInfo> getInstalledPlugins();
    
    void loadPluginArchive(File plugin) throws IOException;
}
