package org.mule.galaxy.impl;

import org.mule.galaxy.Settings;

public class SettingsImpl implements Settings {

    public String getInitialDocumentVersion() {
        return "0.0.1";
    }

    public String getInitialServiceVersion() {
        return "0.0.1";
    }

    public String getDefaultWorkspaceName() {
        return "Default Workspace";
    }

}
