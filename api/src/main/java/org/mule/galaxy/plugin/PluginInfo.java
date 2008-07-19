package org.mule.galaxy.plugin;

import org.mule.galaxy.Identifiable;


public class PluginInfo implements Identifiable {
    private String id;
    private String plugin;
    // this is a long because jcr doesn't store int values
    private long version = -1;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPlugin() {
        return plugin;
    }
    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
}
