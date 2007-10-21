package org.mule.galaxy;

public interface Settings {
    String getNextVersion(String version);
    String getInitialVersion();
    String getDefaultWorkspaceName();
}
