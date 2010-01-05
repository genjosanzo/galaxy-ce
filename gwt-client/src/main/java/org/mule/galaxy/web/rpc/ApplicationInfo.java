package org.mule.galaxy.web.rpc;

import java.io.Serializable;
import java.util.Collection;

public class ApplicationInfo implements Serializable {
    private boolean userManagementSupported;
    private WUser user;
    private Collection<PluginTabInfo> pluginTabs;
    private Collection<WExtensionInfo> extensions;
    
    public boolean isUserManagementSupported() {
        return userManagementSupported;
    }
    public void setUserManagementSupported(boolean userManagementSupported) {
        this.userManagementSupported = userManagementSupported;
    }
    public WUser getUser() {
        return user;
    }
    public void setUser(WUser user) {
        this.user = user;
    }
    public Collection<PluginTabInfo> getPluginTabs() {
        return pluginTabs;
    }
    public void setPluginTabs(Collection<PluginTabInfo> pluginTabs) {
        this.pluginTabs = pluginTabs;
    }
    public Collection<WExtensionInfo> getExtensions() {
        return extensions;
    }
    public void setExtensions(Collection<WExtensionInfo> extensions) {
        this.extensions = extensions;
    }
    
}
