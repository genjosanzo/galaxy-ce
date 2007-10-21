package org.mule.galaxy.impl;

import org.mule.galaxy.Settings;

public class SettingsImpl implements Settings {

    
    public String getNextVersion(String version) {
        // TODO refactor into a version service
        try {
            int v = Integer.parseInt(version);
            
            return new Integer(v++).toString();
        } catch (NumberFormatException e) {
            return version + "-copy";
        }
    }

    public String getInitialVersion() {
        return "1";
    }

    public String getDefaultWorkspaceName() {
        return "Default Workspace";
    }

}
