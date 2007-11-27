package org.mule.galaxy.impl.view;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.view.ArtifactView;
import org.mule.galaxy.view.ViewLink;

public class DefaultArtifactView implements ArtifactView {

    public String[] getColumnNames() {
        return new String [] {
          "Name",
          "Version",
          "Phase"
        };
    }

    public String getColumnValue(Artifact row, int i) {
        switch (i) {
        case 0:
            return row.getName();
        case 1:
            return row.getLatestVersion().getVersionLabel();
        case 2:
            return row.getPhase().getName();
        }
        
        return null;
    }

    public ViewLink getLink(Artifact row, int i) {
        return null;
    }
}
