package org.mule.galaxy.view;

import org.mule.galaxy.Artifact;

public class DefaultArtifactTypeView implements ArtifactTypeView {

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
            return row.getActiveVersion().getVersionLabel();
        case 2:
            return row.getPhase().getName();
        }
        
        return null;
    }

    public ViewLink getLink(Artifact row, int i) {
        return null;
    }

    public boolean isSummaryOnly(int column) {
        return false;
    }
}
