package org.mule.galaxy.impl.render;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.render.ArtifactRenderer;

public class DefaultArtifactRenderer implements ArtifactRenderer {

    public String[] getColumnNames() {
        return new String [] {
          "Name",
          "Workspace",
          "Version",
          "Phase"
        };
    }

    public String getColumnValue(Artifact row, int i) {
        switch (i) {
        case 0:
            return row.getName();
        case 1:
            return row.getParent().getPath();
        case 2:
            return row.getDefaultOrLastVersion().getVersionLabel();
        case 3:
            return row.getDefaultOrLastVersion().getPhase().getName();
        }
        
        return null;
    }

    public boolean isSummary(int column) {
        return column != 1;
    }

    public boolean isDetail(int column) {
        return true;
    }
    
}
